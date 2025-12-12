package com.sailtrack.backend.controller;

import com.sailtrack.backend.dto.LeaveApprovalRequest;
import com.sailtrack.backend.dto.LeaveRequest;
import com.sailtrack.backend.entity.LeaveRecord;
import com.sailtrack.backend.service.LeaveService;
import com.sailtrack.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leave")
@RequiredArgsConstructor
public class LeaveController {
    
    private final LeaveService leaveService;
    private final JwtUtil jwtUtil;
    
    @PostMapping("/apply")
    public Map<String, Object> applyLeave(@Valid @RequestBody LeaveRequest request,
                                         HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("请先登录");
        }
        
        Long leaveId = leaveService.applyLeave(userId, request);
        return Map.of("ok", true, "message", "请假申请提交成功", "leaveId", leaveId);
    }
    
    @PostMapping("/approve")
    public Map<String, Object> approveLeave(@Valid @RequestBody LeaveApprovalRequest request,
                                           HttpServletRequest httpRequest) {
        Long approverId = (Long) httpRequest.getAttribute("userId");
        if (approverId == null) {
            throw new RuntimeException("请先登录");
        }
        
        leaveService.approveLeave(approverId, request);
        
        String message = request.getStatus() == 1 ? "请假已批准" : "请假已拒绝";
        return Map.of("ok", true, "message", message);
    }
    
    @GetMapping("/my-records")
    public Map<String, Object> getMyLeaveRecords(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("请先登录");
        }
        
        List<LeaveRecord> records = leaveService.getUserLeaveRecords(userId);
        return Map.of("ok", true, "data", records);
    }
    
    @GetMapping("/pending-approvals")
    public Map<String, Object> getPendingApprovals(HttpServletRequest request) {
        Long managerId = (Long) request.getAttribute("userId");
        if (managerId == null) {
            throw new RuntimeException("请先登录");
        }
        
        List<LeaveRecord> records = leaveService.getPendingApprovals(managerId);
        return Map.of("ok", true, "data", records);
    }
    
    @GetMapping("/{id}")
    public Map<String, Object> getLeaveRecord(@PathVariable Long id,
                                           HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("请先登录");
        }
        
        LeaveRecord record = leaveService.getLeaveRecord(id);
        return Map.of("ok", true, "data", record);
    }
}