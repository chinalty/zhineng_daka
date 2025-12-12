package com.sailtrack.backend.controller;

import com.sailtrack.backend.dto.CheckInRequest;
import com.sailtrack.backend.entity.AttendanceRecord;
import com.sailtrack.backend.service.AttendanceService;
import com.sailtrack.backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    
    private final AttendanceService attendanceService;
    private final JwtUtil jwtUtil;
    
    @PostMapping("/check")
    public Map<String, Object> checkInOut(@RequestBody CheckInRequest request,
                                         HttpServletRequest httpRequest) {
        // 从拦截器获取用户信息
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("请先登录");
        }
        
        AttendanceRecord record;
        if (request.getType() == 1) {
            record = attendanceService.checkIn(userId, httpRequest);
            return Map.of("ok", true, "message", "签到成功", "data", record);
        } else if (request.getType() == 2) {
            record = attendanceService.checkOut(userId, httpRequest);
            return Map.of("ok", true, "message", "签退成功", "data", record);
        } else {
            throw new RuntimeException("无效的打卡类型");
        }
    }
    
    @GetMapping("/today")
    public Map<String, Object> getTodayAttendance(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("请先登录");
        }
        
        AttendanceRecord record = attendanceService.getTodayAttendance(userId);
        return Map.of("ok", true, "data", record);
    }
    
    @GetMapping("/monthly-records")
    public Map<String, Object> getMonthlyRecords(@RequestParam @DateTimeFormat(pattern = "yyyy-MM") String month,
                                               HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("请先登录");
        }
        
        // 解析月份参数
        String[] parts = month.split("-");
        int year = Integer.parseInt(parts[0]);
        int monthValue = Integer.parseInt(parts[1]);
        
        LocalDate startDate = LocalDate.of(year, monthValue, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        
        List<AttendanceRecord> records = attendanceService.getMonthlyRecords(userId, startDate, endDate);
        return Map.of("ok", true, "data", records);
    }
    
    @GetMapping("/monthly-stats")
    public Map<String, Object> getMonthlyStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM") String month,
                                             HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("请先登录");
        }
        
        // 解析月份参数
        String[] parts = month.split("-");
        int year = Integer.parseInt(parts[0]);
        int monthValue = Integer.parseInt(parts[1]);
        
        LocalDate startDate = LocalDate.of(year, monthValue, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        
        Map<String, Object> stats = attendanceService.getMonthlyStats(userId, startDate, endDate);
        return Map.of("ok", true, "data", stats);
    }
    
    @GetMapping("/recent-records")
    public Map<String, Object> getRecentRecords(@RequestParam(defaultValue = "10") int limit,
                                              HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("请先登录");
        }
        
        List<AttendanceRecord> records = attendanceService.getRecentRecords(userId, limit);
        return Map.of("ok", true, "data", records);
    }
}