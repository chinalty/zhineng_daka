package com.sailtrack.backend.service;

import com.sailtrack.backend.dto.LeaveApprovalRequest;
import com.sailtrack.backend.dto.LeaveRequest;
import com.sailtrack.backend.entity.LeaveRecord;
import com.sailtrack.backend.entity.User;
import com.sailtrack.backend.repository.LeaveRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LeaveService {
    private final LeaveRecordRepository leaveRecordRepository;
    private final UserService userService;
    
    public LeaveService(LeaveRecordRepository leaveRecordRepository, UserService userService) {
        this.leaveRecordRepository = leaveRecordRepository;
        this.userService = userService;
    }
    
    @Transactional
    public Long applyLeave(Long userId, LeaveRequest request) {
        User user = userService.getUserById(userId);
        
        // 检查日期范围是否有冲突
        if (leaveRecordRepository.existsByUserIdAndDateRange(
                userId, request.getEndDate(), request.getStartDate())) {
            throw new RuntimeException("请假日期范围内已有请假记录");
        }
        
        // 计算请假天数（排除周末）
        long leaveDays = calculateWorkingDays(request.getStartDate(), request.getEndDate());
        
        LeaveRecord leaveRecord = new LeaveRecord();
        leaveRecord.setUserId(userId);
        leaveRecord.setLeaveType(request.getLeaveType());
        leaveRecord.setStartDate(request.getStartDate());
        leaveRecord.setEndDate(request.getEndDate());
        leaveRecord.setLeaveDays(BigDecimal.valueOf(leaveDays));
        leaveRecord.setReason(request.getReason());
        leaveRecord.setStatus(0); // 待审批
        leaveRecord.setCreatedAt(java.time.LocalDateTime.now());
        
        LeaveRecord saved = leaveRecordRepository.save(leaveRecord);
        return saved.getId();
    }
    
    @Transactional
    public void approveLeave(Long approverId, LeaveApprovalRequest request) {
        LeaveRecord leaveRecord = leaveRecordRepository.findById(request.getLeaveId())
                .orElseThrow(() -> new RuntimeException("请假记录不存在"));
        
        if (leaveRecord.getStatus() != 0) {
            throw new RuntimeException("该请假记录已处理");
        }
        
        leaveRecord.setStatus(request.getStatus());
        leaveRecord.setApproverId(approverId);
        leaveRecord.setApprovalRemark(request.getRemark());
        leaveRecord.setApprovalTime(java.time.LocalDateTime.now());
        
        leaveRecordRepository.save(leaveRecord);
    }
    
    public List<LeaveRecord> getMyLeaveRecords(Long userId) {
        return leaveRecordRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public List<LeaveRecord> getPendingApprovals(Long managerId) {
        User manager = userService.getUserById(managerId);
        
        // 只有部门经理可以审批本部门的请假
        if (manager.getRoleId() != 2) { // 非部门经理
            return List.of();
        }
        
        return leaveRecordRepository.findPendingByDepartmentId(manager.getDepartmentId());
    }
    
    public LeaveRecord getLeaveDetail(Long leaveId) {
        return leaveRecordRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("请假记录不存在"));
    }
    
    private long calculateWorkingDays(LocalDate startDate, LocalDate endDate) {
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long workingDays = 0;
        
        for (int i = 0; i < days; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            
            // 排除周六日
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                workingDays++;
            }
        }
        
        return workingDays;
    }
    
    public List<LeaveRecord> getDepartmentLeaveRecords(Long departmentId) {
        return leaveRecordRepository.findByDepartmentIdOrderByCreatedAtDesc(departmentId);
    }
}