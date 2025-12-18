package com.sailtrack.backend.service;

import com.sailtrack.backend.dto.CheckInRequest;
import com.sailtrack.backend.dto.SupplementApplyRequest;
import com.sailtrack.backend.dto.SupplementApprovalRequest;
import com.sailtrack.backend.entity.AttendanceRecord;
import com.sailtrack.backend.entity.User;
import com.sailtrack.backend.repository.AttendanceRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AttendanceService {
    private final AttendanceRecordRepository attendanceRepository;
    private final UserService userService;
    
    public AttendanceService(AttendanceRecordRepository attendanceRepository, 
                           UserService userService) {
        this.attendanceRepository = attendanceRepository;
        this.userService = userService;
    }
    
    @Transactional
    public AttendanceRecord checkIn(Long userId, CheckInRequest request) {
        // 检查用户是否已上传人脸照片
        User user = userService.getUserById(userId);
        if (user.getFaceImageUrl() == null || user.getFaceImageUrl().isEmpty()) {
            throw new RuntimeException("请先在个人中心上传人脸照片");
        }
        
        LocalDate today = LocalDate.now();
        
        Optional<AttendanceRecord> existingRecord = attendanceRepository
                .findByUserIdAndAttendanceDate(userId, today);
        
        if (request.getType() == 1) { // 签到
            if (existingRecord.isPresent() && existingRecord.get().getCheckInTime() != null) {
                throw new RuntimeException("今日已签到");
            }
            
            AttendanceRecord record = existingRecord.orElse(new AttendanceRecord());
            record.setUserId(userId);
            record.setAttendanceDate(today);
            record.setCheckInTime(LocalDateTime.now());
            record.setCheckInIp("127.0.0.1"); // TODO: 获取真实IP
            
            // 设置默认工作时长为0，签退时会重新计算
            if (record.getWorkHours() == null) {
                record.setWorkHours(BigDecimal.ZERO);
            }
            // 设置默认加班时长为0
            if (record.getOvertimeHours() == null) {
                record.setOvertimeHours(0.0);
            }
            
            // 计算预期签退时间（考虑弹性工作制）
            LocalDateTime signInTime = record.getCheckInTime();
            LocalTime checkInLocalTime = signInTime.toLocalTime();
            LocalDateTime expectedCheckOut;
            
            // 如果是凌晨0-4点签到，预期签退时间应该是当天8-12点（不跨天）
            if (checkInLocalTime.isBefore(LocalTime.of(4, 0))) {
                // 凌晨签到：当天8点 + 签到的小时和分钟数
                LocalDate checkInDate = signInTime.toLocalDate();
                expectedCheckOut = LocalDateTime.of(
                    checkInDate, 
                    LocalTime.of(8, 0).plusHours(checkInLocalTime.getHour()).plusMinutes(checkInLocalTime.getMinute())
                );
            } else {
                // 正常时间签到：签到时间+8小时（可能跨天）
                expectedCheckOut = signInTime.plusHours(8);
            }
            
            record.setExpectedCheckOutTime(expectedCheckOut);
            
            // 判断是否迟到（10点后签到算迟到）
            LocalTime checkInTime = record.getCheckInTime().toLocalTime();
            if (checkInTime.isAfter(LocalTime.of(10, 0))) {
                record.setIsLate(true);
                record.setStatus(2); // 迟到
                record.setLateMinutes((int) java.time.Duration.between(
                        LocalTime.of(10, 0), checkInTime).toMinutes());
            } else {
                record.setStatus(1); // 正常
            }
            
            if (record.getCreatedAt() == null) {
                record.setCreatedAt(LocalDateTime.now());
            }
            
            return attendanceRepository.save(record);
            
        } else if (request.getType() == 2) { // 签退
            if (existingRecord.isEmpty() || existingRecord.get().getCheckInTime() == null) {
                throw new RuntimeException("请先签到");
            }
            
            AttendanceRecord record = existingRecord.get();
            if (record.getCheckOutTime() != null) {
                throw new RuntimeException("今日已签退");
            }
            
            record.setCheckOutTime(LocalDateTime.now());
            record.setCheckOutIp("127.0.0.1"); // TODO: 获取真实IP
            
            // 计算工作时长
            LocalDateTime checkInTime = record.getCheckInTime();
            LocalDateTime checkOutTime = record.getCheckOutTime();
            
            // 计算实际工作时长（小时）
            long minutes = java.time.Duration.between(checkInTime, checkOutTime).toMinutes();
            double hours = minutes / 60.0;
            
            // 使用 BigDecimal 确保精度
            BigDecimal workHours = BigDecimal.valueOf(hours).setScale(2, BigDecimal.ROUND_HALF_UP);
            record.setWorkHours(workHours);
            
            // 计算加班时长（超过8小时的部分）
            if (hours > 8) {
                record.setOvertimeHours(hours - 8);
            } else {
                record.setOvertimeHours(0.0);
            }
            
            // 调试日志
            System.out.println("=== 工作时长计算 ===");
            System.out.println("用户ID: " + userId);
            System.out.println("签到时间: " + checkInTime);
            System.out.println("签退时间: " + checkOutTime);
            System.out.println("工作分钟数: " + minutes);
            System.out.println("工作小时数: " + workHours);
            
            // 判断是否早退
            if (record.getExpectedCheckOutTime() != null && 
                checkOutTime.isBefore(record.getExpectedCheckOutTime())) {
                record.setIsEarlyLeave(true);
                record.setStatus(3); // 早退
                record.setEarlyLeaveMinutes((int) java.time.Duration.between(
                        checkOutTime, record.getExpectedCheckOutTime()).toMinutes());
            } else if (record.getStatus() != 2) { // 如果不是迟到，则设为正常
                record.setStatus(1);
            }
            
            return attendanceRepository.save(record);
            
        } else {
            throw new RuntimeException("无效的打卡类型");
        }
    }
    
    public Optional<AttendanceRecord> getTodayAttendance(Long userId) {
        return attendanceRepository.findByUserIdAndAttendanceDate(userId, LocalDate.now());
    }
    
    public List<AttendanceRecord> getMonthlyRecords(Long userId, String month) {
        // 解析月份字符串 (YYYY-MM)
        String[] parts = month.split("-");
        int year = Integer.parseInt(parts[0]);
        int monthValue = Integer.parseInt(parts[1]);
        
        LocalDate startDate = LocalDate.of(year, monthValue, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        
        return attendanceRepository.findByUserIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                userId, startDate, endDate);
    }
    
    public Map<String, Object> getMonthlyStats(Long userId, String month) {
        List<AttendanceRecord> records = getMonthlyRecords(userId, month);
        
        Map<String, Object> stats = Map.of(
            "totalDays", records.size(),
            "totalHours", records.stream()
                .filter(r -> r.getWorkHours() != null)
                .mapToDouble(r -> r.getWorkHours().doubleValue())
                .sum()
        );
        
        return stats;
    }
    
    public List<AttendanceRecord> getRecentRecords(Long userId, int limit) {
        List<AttendanceRecord> records = attendanceRepository.findRecentByUserId(userId);
        return records.stream().limit(limit).toList();
    }
    
    // ===== 补卡申请相关方法 =====
    
    @Transactional
    public AttendanceRecord applySupplement(Long userId, SupplementApplyRequest request) {
        // 解析目标日期
        LocalDate targetDate = LocalDate.parse(request.getTargetDate());
        
        // 检查是否已经存在相同的补卡申请
        Optional<AttendanceRecord> existingRecord = attendanceRepository
                .findByUserIdAndAttendanceDate(userId, targetDate);
        
        if (existingRecord.isPresent()) {
            AttendanceRecord record = existingRecord.get();
            // 检查是否已有待审批的补卡申请
            if (record.getIsSupplement() != null && record.getIsSupplement() && 
                record.getSupplementStatus() != null && record.getSupplementStatus() == 0) {
                throw new RuntimeException("该日期已有待审批的补卡申请");
            }
            // 检查是否已有正常的考勤记录
            if (request.getCheckType() == 1 && record.getCheckInTime() != null) {
                throw new RuntimeException("该日期已有签到记录");
            }
            if (request.getCheckType() == 2 && record.getCheckOutTime() != null) {
                throw new RuntimeException("该日期已有签退记录");
            }
        }
        
        // 获取或创建考勤记录
        AttendanceRecord attendanceRecord = existingRecord.orElse(new AttendanceRecord());
        attendanceRecord.setUserId(userId);
        attendanceRecord.setAttendanceDate(targetDate);
        
        // 解析补卡时间
        String[] timeParts = request.getCheckTime().split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        
        LocalDateTime supplementTime = LocalDateTime.of(targetDate, java.time.LocalTime.of(hour, minute));
        
        // 设置补卡信息
        attendanceRecord.setIsSupplement(true);
        attendanceRecord.setSupplementStatus(0); // 待审批
        attendanceRecord.setSupplementReason(request.getReason());
        
        if (request.getCheckType() == 1) { // 补签到
            attendanceRecord.setCheckInTime(supplementTime);
            attendanceRecord.setCheckInIp("补卡申请");
        } else { // 补签退
            attendanceRecord.setCheckOutTime(supplementTime);
            attendanceRecord.setCheckOutIp("补卡申请");
        }
        
        if (attendanceRecord.getCreatedAt() == null) {
            attendanceRecord.setCreatedAt(LocalDateTime.now());
        }
        
        return attendanceRepository.save(attendanceRecord);
    }
    
    public List<AttendanceRecord> getSupplementRecords(Long userId) {
        return attendanceRepository.findByUserIdOrderByAttendanceDateDesc(userId).stream()
                .filter(record -> record.getIsSupplement() != null && record.getIsSupplement())
                .toList();
    }
    
    @Transactional
    public void deleteSupplementRecord(Long userId, Long recordId) {
        AttendanceRecord record = attendanceRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("补卡申请不存在"));
        
        if (!record.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除此申请");
        }
        
        if (record.getSupplementStatus() == null || record.getSupplementStatus() != 0) {
            throw new RuntimeException("只能撤销待审批的申请");
        }
        
        // 如果是补签到，清空签到时间
        if (record.getCheckInIp() != null && record.getCheckInIp().equals("补卡申请")) {
            record.setCheckInTime(null);
        }
        // 如果是补签退，清空签退时间
        if (record.getCheckOutIp() != null && record.getCheckOutIp().equals("补卡申请")) {
            record.setCheckOutTime(null);
        }
        
        // 清空补卡相关信息
        record.setIsSupplement(false);
        record.setSupplementStatus(null);
        record.setSupplementReason(null);
        
        attendanceRepository.save(record);
    }
    
    @Transactional
    public AttendanceRecord approveSupplement(Long approverId, SupplementApprovalRequest request) {
        AttendanceRecord record = attendanceRepository.findById(request.getApplyId())
                .orElseThrow(() -> new RuntimeException("补卡申请不存在"));
        
        if (record.getSupplementStatus() == null || record.getSupplementStatus() != 0) {
            throw new RuntimeException("该申请已被处理");
        }
        
        // 更新审批信息
        record.setSupplementStatus(request.getStatus());
        record.setApproverId(approverId);
        record.setApprovalTime(LocalDateTime.now());
        record.setApprovalRemark(request.getRemark());
        
        // 如果批准，更新考勤记录
        if (request.getStatus() == 1) {
            processApprovedSupplement(record);
        } else {
            // 如果拒绝，清空补卡时间
            if (record.getCheckInIp() != null && record.getCheckInIp().equals("补卡申请")) {
                record.setCheckInTime(null);
            }
            if (record.getCheckOutIp() != null && record.getCheckOutIp().equals("补卡申请")) {
                record.setCheckOutTime(null);
            }
        }
        
        return attendanceRepository.save(record);
    }
    
    public List<AttendanceRecord> getPendingApprovals(Long managerId) {
        User manager = userService.getUserById(managerId);
        
        List<AttendanceRecord> allPending = attendanceRepository.findAll().stream()
                .filter(record -> record.getIsSupplement() != null && record.getIsSupplement() && 
                                record.getSupplementStatus() != null && record.getSupplementStatus() == 0)
                .toList();
        
        // 如果是管理员，查看所有待审批
        if (manager.getRoleId() == 1) { // ADMIN
            return allPending;
        }
        
        // 如果是部门经理，查看本部门待审批
        if (manager.getRoleId() == 2) { // MANAGER
            return allPending.stream()
                    .filter(record -> {
                        User recordUser = userService.getUserById(record.getUserId());
                        return recordUser.getDepartmentId().equals(manager.getDepartmentId());
                    })
                    .toList();
        }
        
        throw new RuntimeException("无权限查看待审批列表");
    }
    
    private void processApprovedSupplement(AttendanceRecord record) {
        // 更新IP地址为正式补卡
        if (record.getCheckInIp() != null && record.getCheckInIp().equals("补卡申请")) {
            record.setCheckInIp("补卡");
            
            // 计算预期签退时间
            LocalDateTime checkInTime = record.getCheckInTime();
            LocalTime checkInLocalTime = checkInTime.toLocalTime();
            LocalDateTime expectedCheckOut;
            
            if (checkInLocalTime.isBefore(LocalTime.of(4, 0))) {
                LocalDate checkInDate = checkInTime.toLocalDate();
                expectedCheckOut = LocalDateTime.of(
                    checkInDate, 
                    LocalTime.of(8, 0).plusHours(checkInLocalTime.getHour()).plusMinutes(checkInLocalTime.getMinute())
                );
            } else {
                expectedCheckOut = checkInTime.plusHours(8);
            }
            
            record.setExpectedCheckOutTime(expectedCheckOut);
            
            // 判断是否迟到
            if (checkInLocalTime.isAfter(LocalTime.of(10, 0))) {
                record.setIsLate(true);
                record.setStatus(2); // 迟到
                record.setLateMinutes((int) java.time.Duration.between(
                        LocalTime.of(10, 0), checkInLocalTime).toMinutes());
            } else {
                record.setStatus(1); // 正常
            }
        }
        
        if (record.getCheckOutIp() != null && record.getCheckOutIp().equals("补卡申请")) {
            record.setCheckOutIp("补卡");
            
            // 计算工作时长
            if (record.getCheckInTime() != null) {
                LocalDateTime checkInTime = record.getCheckInTime();
                LocalDateTime checkOutTime = record.getCheckOutTime();
                long minutes = java.time.Duration.between(checkInTime, checkOutTime).toMinutes();
                double hours = minutes / 60.0;
                BigDecimal workHours = BigDecimal.valueOf(hours).setScale(2, BigDecimal.ROUND_HALF_UP);
                record.setWorkHours(workHours);
                
                // 计算加班时长（超过8小时的部分）
                if (hours > 8) {
                    record.setOvertimeHours(hours - 8);
                } else {
                    record.setOvertimeHours(0.0);
                }
                
                // 判断是否早退
                if (record.getExpectedCheckOutTime() != null && 
                    checkOutTime.isBefore(record.getExpectedCheckOutTime())) {
                    record.setIsEarlyLeave(true);
                    record.setStatus(3); // 早退
                    record.setEarlyLeaveMinutes((int) java.time.Duration.between(
                            checkOutTime, record.getExpectedCheckOutTime()).toMinutes());
                } else if (record.getStatus() != 2) { // 如果不是迟到，则设为正常
                    record.setStatus(1);
                }
            }
        }
    }
}