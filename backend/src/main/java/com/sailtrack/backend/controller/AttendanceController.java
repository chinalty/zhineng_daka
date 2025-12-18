package com.sailtrack.backend.controller;

import com.sailtrack.backend.dto.CheckInRequest;
import com.sailtrack.backend.dto.SupplementApplyRequest;
import com.sailtrack.backend.dto.SupplementApprovalRequest;
import com.sailtrack.backend.entity.AttendanceRecord;
import com.sailtrack.backend.entity.User;
import com.sailtrack.backend.service.AttendanceService;
import com.sailtrack.backend.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;
    private final JwtUtil jwtUtil;
    private final com.sailtrack.backend.service.FaceRecognitionService faceRecognitionService;
    private final com.sailtrack.backend.service.UserService userService;
    
    public AttendanceController(AttendanceService attendanceService, JwtUtil jwtUtil,
                               com.sailtrack.backend.service.FaceRecognitionService faceRecognitionService,
                               com.sailtrack.backend.service.UserService userService) {
        this.attendanceService = attendanceService;
        this.jwtUtil = jwtUtil;
        this.faceRecognitionService = faceRecognitionService;
        this.userService = userService;
    }
    
    @PostMapping("/check")
    public Map<String, Object> checkIn(HttpServletRequest request,
                                       @Valid @RequestBody CheckInRequest dto) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        // 检查用户是否已上传人脸照片
        com.sailtrack.backend.entity.User user = userService.getUserById(userId);
        if (user.getFaceImageUrl() == null || user.getFaceImageUrl().isEmpty()) {
            throw new RuntimeException("请先在个人中心上传人脸照片");
        }
        
        AttendanceRecord record = attendanceService.checkIn(userId, dto);
        
        return Map.of("ok", true, "data", record);
    }
    
    @GetMapping("/today")
    public Map<String, Object> getTodayAttendance(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        Optional<AttendanceRecord> record = attendanceService.getTodayAttendance(userId);
        
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("ok", true);
        
        if (record.isPresent()) {
            AttendanceRecord attendanceRecord = record.get();
            // 确保 workHours 不为 null
            if (attendanceRecord.getWorkHours() == null) {
                attendanceRecord.setWorkHours(java.math.BigDecimal.ZERO);
            }
            response.put("data", attendanceRecord);
        } else {
            response.put("data", null);
        }
        
        return response;
    }
    
    @GetMapping("/monthly-records")
    public Map<String, Object> getMonthlyRecords(HttpServletRequest request,
                                                 @RequestParam String month) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        List<AttendanceRecord> records = attendanceService.getMonthlyRecords(userId, month);
        
        return Map.of("ok", true, "data", records);
    }
    
    @GetMapping("/monthly-stats")
    public Map<String, Object> getMonthlyStats(HttpServletRequest request,
                                              @RequestParam String month) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        Map<String, Object> stats = attendanceService.getMonthlyStats(userId, month);
        
        return Map.of("ok", true, "data", stats);
    }
    
    @GetMapping("/recent-records")
    public Map<String, Object> getRecentRecords(HttpServletRequest request,
                                                @RequestParam(defaultValue = "10") int limit) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        List<AttendanceRecord> records = attendanceService.getRecentRecords(userId, limit);
        
        return Map.of("ok", true, "data", records);
    }
    
    @PostMapping("/check-with-face")
    public Map<String, Object> checkInWithFace(HttpServletRequest request,
                                               @RequestParam("type") Integer type,
                                               @RequestParam("faceImage") org.springframework.web.multipart.MultipartFile faceImage) {
        if (!faceRecognitionService.isConfigured()) {
            throw new RuntimeException("人脸识别功能未配置，请使用普通打卡");
        }
        
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        com.sailtrack.backend.entity.User user = userService.getUserById(userId);
        
        if (user.getFaceImageUrl() == null || user.getFaceImageUrl().isEmpty()) {
            throw new RuntimeException("请先在个人中心上传人脸照片");
        }
        
        try {
            System.out.println("=== 开始人脸识别打卡流程 ===");
            System.out.println("用户ID: " + userId);
            System.out.println("底库照片URL: " + user.getFaceImageUrl());
            
            // 1. 上传当前拍摄的照片到OSS，获取图片链接
            String currentFaceUrl = faceRecognitionService.uploadFaceImage(faceImage, userId);
            System.out.println("当前照片URL: " + currentFaceUrl);
            
            // 2. 调用阿里云人脸识别服务进行对比
            boolean matched = faceRecognitionService.compareFace(
                user.getFaceImageUrl(),
                currentFaceUrl
            );
            
            System.out.println("人脸识别结果: " + (matched ? "匹配成功" : "匹配失败"));
            
            if (!matched) {
                throw new RuntimeException("人脸识别失败，请重试或使用普通打卡");
            }
            
            // 3. 识别成功，记录考勤
            CheckInRequest checkInRequest = new CheckInRequest();
            checkInRequest.setType(type);
            AttendanceRecord record = attendanceService.checkIn(userId, checkInRequest);
            
            System.out.println("考勤记录成功，ID: " + record.getId());
            System.out.println("=== 人脸识别打卡流程完成 ===");
            
            return Map.of("ok", true, "message", "人脸识别成功，打卡完成", "data", record);
            
        } catch (Exception e) {
            System.err.println("人脸识别打卡失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("人脸识别打卡失败: " + e.getMessage());
        }
    }
    
    // ===== 补卡申请相关接口 =====
    
    @GetMapping("/supplement-records")
    public Map<String, Object> getSupplementRecords(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        List<AttendanceRecord> records = attendanceService.getSupplementRecords(userId);
        
        return Map.of("ok", true, "data", records);
    }
    
    @PostMapping("/supplement-apply")
    public Map<String, Object> applySupplement(HttpServletRequest request,
                                              @Valid @RequestBody SupplementApplyRequest dto) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        AttendanceRecord record = attendanceService.applySupplement(userId, dto);
        
        return Map.of("ok", true, "message", "补卡申请提交成功", "data", record);
    }
    
    @DeleteMapping("/supplement-apply/{id}")
    public Map<String, Object> deleteSupplementApply(HttpServletRequest request,
                                                     @PathVariable Long id) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        attendanceService.deleteSupplementRecord(userId, id);
        
        return Map.of("ok", true, "message", "补卡申请撤销成功");
    }
    
    @PostMapping("/supplement-approve")
    public Map<String, Object> approveSupplement(HttpServletRequest request,
                                                @Valid @RequestBody SupplementApprovalRequest dto) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long approverId = jwtUtil.getUserIdFromToken(token);
        AttendanceRecord record = attendanceService.approveSupplement(approverId, dto);
        
        String message = record.getSupplementStatus() == 1 ? "补卡申请批准成功" : "补卡申请拒绝成功";
        
        return Map.of("ok", true, "message", message, "data", record);
    }
    
    @GetMapping("/supplement-pending")
    public Map<String, Object> getPendingSupplements(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long managerId = jwtUtil.getUserIdFromToken(token);
        List<AttendanceRecord> records = attendanceService.getPendingApprovals(managerId);
        
        return Map.of("ok", true, "data", records);
    }
}