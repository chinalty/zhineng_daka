package com.sailtrack.backend.service;

import com.sailtrack.backend.dto.CheckInRequest;
import com.sailtrack.backend.entity.AttendanceRecord;
import com.sailtrack.backend.entity.User;
import com.sailtrack.backend.repository.AttendanceRecordRepository;
import com.sailtrack.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final UserRepository userRepository;
    
    // 弹性工作制配置
    private static final LocalTime FLEX_CHECK_IN_START = LocalTime.of(8, 0);
    private static final LocalTime FLEX_CHECK_IN_END = LocalTime.of(10, 0);
    private static final double REQUIRED_WORK_HOURS = 8.0;
    
    @Transactional
    public AttendanceRecord checkIn(Long userId, HttpServletRequest request) {
        LocalDate today = LocalDate.now();
        Optional<AttendanceRecord> existingRecord = attendanceRecordRepository.findByUserIdAndAttendanceDate(userId, today);
        
        if (existingRecord.isPresent() && existingRecord.get().getCheckInTime() != null) {
            throw new RuntimeException("今日已签到");
        }
        
        AttendanceRecord record = existingRecord.orElseGet(() -> {
            AttendanceRecord newRecord = new AttendanceRecord();
            newRecord.setUserId(userId);
            newRecord.setAttendanceDate(today);
            return newRecord;
        });
        
        LocalDateTime now = LocalDateTime.now();
        record.setCheckInTime(now);
        record.setCheckInIp(getClientIpAddress(request));
        
        // 判断是否迟到
        if (now.toLocalTime().isAfter(FLEX_CHECK_IN_END)) {
            record.setIsLate(true);
            record.setLateMinutes((int) Duration.between(FLEX_CHECK_IN_END, now.toLocalTime()).toMinutes());
            record.setStatus(2); // 迟到
        } else {
            record.setIsLate(false);
            record.setLateMinutes(0);
            record.setStatus(1); // 正常
        }
        
        // 计算预期签退时间（弹性工作制）
        LocalDateTime expectedCheckOut = now.plusHours((long) REQUIRED_WORK_HOURS);
        record.setExpectedCheckOutTime(expectedCheckOut);
        
        return attendanceRecordRepository.save(record);
    }
    
    @Transactional
    public AttendanceRecord checkOut(Long userId, HttpServletRequest request) {
        LocalDate today = LocalDate.now();
        Optional<AttendanceRecord> existingRecord = attendanceRecordRepository.findByUserIdAndAttendanceDate(userId, today);
        
        if (existingRecord.isEmpty()) {
            throw new RuntimeException("请先签到");
        }
        
        AttendanceRecord record = existingRecord.get();
        if (record.getCheckOutTime() != null) {
            throw new RuntimeException("今日已签退");
        }
        
        LocalDateTime now = LocalDateTime.now();
        record.setCheckOutTime(now);
        record.setCheckOutIp(getClientIpAddress(request));
        
        // 计算工作时长
        if (record.getCheckInTime() != null) {
            Duration workDuration = Duration.between(record.getCheckInTime(), now);
            double workHours = workDuration.toMinutes() / 60.0;
            record.setWorkHours(BigDecimal.valueOf(workHours));
            
            // 判断是否早退
            if (record.getExpectedCheckOutTime() != null && now.isBefore(record.getExpectedCheckOutTime())) {
                record.setIsEarlyLeave(true);
                record.setEarlyLeaveMinutes((int) Duration.between(now, record.getExpectedCheckOutTime()).toMinutes());
                if (record.getStatus() != 2) { // 如果不是迟到，则设为早退
                    record.setStatus(3); // 早退
                }
            }
        }
        
        return attendanceRecordRepository.save(record);
    }
    
    public AttendanceRecord getTodayAttendance(Long userId) {
        LocalDate today = LocalDate.now();
        return attendanceRecordRepository.findByUserIdAndAttendanceDate(userId, today)
                .orElseThrow(() -> new RuntimeException("今日无考勤记录"));
    }
    
    public List<AttendanceRecord> getMonthlyRecords(Long userId, LocalDate startDate, LocalDate endDate) {
        return attendanceRecordRepository.findByUserIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
            userId, startDate, endDate);
    }
    
    public Map<String, Object> getMonthlyStats(Long userId, LocalDate startDate, LocalDate endDate) {
        List<AttendanceRecord> records = getMonthlyRecords(userId, startDate, endDate);
        
        Map<String, Object> stats = new HashMap<>();
        
        // 计算总工作时长
        double totalHours = records.stream()
                .filter(record -> record.getWorkHours() != null)
                .mapToDouble(record -> record.getWorkHours().doubleValue())
                .sum();
        
        // 统计考勤状态
        long normalDays = records.stream().filter(r -> r.getStatus() == 1).count();
        long lateDays = records.stream().filter(r -> r.getIsLate()).count();
        long earlyLeaveDays = records.stream().filter(r -> r.getIsEarlyLeave()).count();
        
        // 应出勤天数（排除周末）
        int workDays = 0;
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            int dayOfWeek = current.getDayOfWeek().getValue();
            if (dayOfWeek <= 5) { // 1-5 是周一到周五
                workDays++;
            }
            current = current.plusDays(1);
        }
        
        // 实际出勤天数（有签到记录的）
        long actualDays = records.stream().filter(r -> r.getCheckInTime() != null).count();
        
        stats.put("totalHours", totalHours);
        stats.put("workDays", workDays);
        stats.put("actualDays", actualDays);
        stats.put("normalDays", normalDays);
        stats.put("lateDays", lateDays);
        stats.put("earlyLeaveDays", earlyLeaveDays);
        stats.put("attendanceRate", workDays > 0 ? (double) actualDays / workDays * 100 : 0);
        
        return stats;
    }
    
    public List<AttendanceRecord> getRecentRecords(Long userId, int limit) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30); // 最近30天
        
        List<AttendanceRecord> records = attendanceRecordRepository
                .findByUserIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(userId, startDate, endDate);
        
        return records.stream().limit(limit).toList();
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}