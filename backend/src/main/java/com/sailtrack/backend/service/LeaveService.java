package com.sailtrack.backend.service;

import com.sailtrack.backend.dto.LeaveApprovalRequest;
import com.sailtrack.backend.dto.LeaveRequest;
import com.sailtrack.backend.entity.LeaveRecord;
import com.sailtrack.backend.entity.User;
import com.sailtrack.backend.repository.LeaveRecordRepository;
import com.sailtrack.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveService {
    
    private final LeaveRecordRepository leaveRecordRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public Long applyLeave(Long userId, LeaveRequest request) {
        // 检查请假时间是否重叠
        Long overlappingCount = leaveRecordRepository.countOverlappingLeaves(
            userId, request.getStartDate(), request.getEndDate());
        
        if (overlappingCount > 0) {
            throw new RuntimeException("请假时间与已有请假记录重叠");
        }
        
        LeaveRecord leaveRecord = new LeaveRecord();
        leaveRecord.setUserId(userId);
        leaveRecord.setLeaveType(request.getLeaveType());
        leaveRecord.setStartDate(request.getStartDate());
        leaveRecord.setEndDate(request.getEndDate());
        leaveRecord.setLeaveDays(request.getLeaveDays());
        leaveRecord.setReason(request.getReason());
        leaveRecord.setStatus(0); // 待审批
        
        return leaveRecordRepository.save(leaveRecord).getId();
    }
    
    @Transactional
    public void approveLeave(Long approverId, LeaveApprovalRequest request) {
        LeaveRecord leaveRecord = leaveRecordRepository.findById(request.getLeaveId())
                .orElseThrow(() -> new RuntimeException("请假记录不存在"));
        
        if (leaveRecord.getStatus() != 0) {
            throw new RuntimeException("该请假已处理");
        }
        
        // 检查审批权限（部门经理可以审批本部门员工的请假）
        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new RuntimeException("审批人不存在"));
        
        User leaveUser = userRepository.findById(leaveRecord.getUserId())
                .orElseThrow(() -> new RuntimeException("请假用户不存在"));
        
        if (!approver.getRoleId().equals(2L) || !approver.getDepartmentId().equals(leaveUser.getDepartmentId())) {
            throw new RuntimeException("无审批权限");
        }
        
        leaveRecord.setStatus(request.getStatus());
        leaveRecord.setApproverId(approverId);
        leaveRecord.setApprovalTime(LocalDateTime.now());
        leaveRecord.setApprovalRemark(request.getRemark());
        
        leaveRecordRepository.save(leaveRecord);
    }
    
    public List<LeaveRecord> getUserLeaveRecords(Long userId) {
        return leaveRecordRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public List<LeaveRecord> getPendingApprovals(Long managerId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("管理员不存在"));
        
        if (!manager.getRoleId().equals(2L)) {
            throw new RuntimeException("无审批权限");
        }
        
        return leaveRecordRepository.findByDepartmentIdAndStatus(manager.getDepartmentId(), 0);
    }
    
    public LeaveRecord getLeaveRecord(Long leaveId) {
        return leaveRecordRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("请假记录不存在"));
    }
}