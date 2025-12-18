package com.sailtrack.backend.controller;

import com.sailtrack.backend.dto.LeaveApprovalRequest;
import com.sailtrack.backend.dto.LeaveRequest;
import com.sailtrack.backend.entity.LeaveRecord;
import com.sailtrack.backend.entity.User;
import com.sailtrack.backend.service.LeaveService;
import com.sailtrack.backend.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/api/leave")
public class LeaveController {
    private final LeaveService leaveService;
    private final JwtUtil jwtUtil;
    private final com.sailtrack.backend.service.UserService userService;
    
    public LeaveController(LeaveService leaveService, JwtUtil jwtUtil, com.sailtrack.backend.service.UserService userService) {
        this.leaveService = leaveService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }
    
    @PostMapping("/apply")
    public Map<String, Object> applyLeave(HttpServletRequest request,
                                         @Valid @RequestBody LeaveRequest dto) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        Long leaveId = leaveService.applyLeave(userId, dto);
        
        return Map.of("ok", true, "data", Map.of("leaveId", leaveId));
    }
    
    @PostMapping("/approve")
    public Map<String, Object> approveLeave(HttpServletRequest request,
                                           @Valid @RequestBody LeaveApprovalRequest dto) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long approverId = jwtUtil.getUserIdFromToken(token);
        leaveService.approveLeave(approverId, dto);
        
        return Map.of("ok", true, "message", "审批完成");
    }
    
    @GetMapping("/my-records")
    public Map<String, Object> getMyLeaveRecords(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        List<LeaveRecord> records = leaveService.getMyLeaveRecords(userId);
        
        return Map.of("ok", true, "data", records);
    }
    
    @GetMapping("/pending-approvals")
    public Map<String, Object> getPendingApprovals(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        List<LeaveRecord> records = leaveService.getPendingApprovals(userId);
        
        // 转换为包含申请人姓名的数据结构
        List<Map<String, Object>> result = new ArrayList<>();
        for (LeaveRecord record : records) {
            Map<String, Object> item = new HashMap<>();
            
            // 获取申请人信息
            User applicant = userService.getUserById(record.getUserId());
            
            item.put("id", record.getId());
            item.put("userId", record.getUserId());
            item.put("applicantName", applicant.getRealName() != null ? applicant.getRealName() : applicant.getUsername());
            item.put("leaveType", record.getLeaveType());
            item.put("startDate", record.getStartDate());
            item.put("endDate", record.getEndDate());
            item.put("leaveDays", record.getLeaveDays());
            item.put("reason", record.getReason());
            item.put("status", record.getStatus());
            item.put("createdAt", record.getCreatedAt());
            
            result.add(item);
        }
        
        return Map.of("ok", true, "data", result);
    }
    
    @GetMapping("/{id}")
    public Map<String, Object> getLeaveDetail(@PathVariable Long id) {
        LeaveRecord record = leaveService.getLeaveDetail(id);
        return Map.of("ok", true, "data", record);
    }
    
    @GetMapping("/department-records")
    public Map<String, Object> getDepartmentLeaveRecords(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long managerId = jwtUtil.getUserIdFromToken(token);
        User manager = userService.getUserById(managerId);

        // 只有部门经理可以查看本部门的请假记录
        if (manager.getRoleId() != 2) { // 非部门经理
            throw new RuntimeException("无权限查看部门请假记录");
        }

        List<LeaveRecord> records = leaveService.getDepartmentLeaveRecords(manager.getDepartmentId());

        // 转换为包含申请人姓名的数据结构
        List<Map<String, Object>> result = new ArrayList<>();
        for (LeaveRecord record : records) {
            Map<String, Object> item = new HashMap<>();

            // 获取申请人信息
            User applicant = userService.getUserById(record.getUserId());

            item.put("id", record.getId());
            item.put("userId", record.getUserId());
            item.put("applicantName", applicant.getRealName() != null ? applicant.getRealName() : applicant.getUsername());
            item.put("leaveType", record.getLeaveType());
            item.put("startDate", record.getStartDate());
            item.put("endDate", record.getEndDate());
            item.put("leaveDays", record.getLeaveDays());
            item.put("reason", record.getReason());
            item.put("status", record.getStatus());
            item.put("createdAt", record.getCreatedAt());
            item.put("approvalTime", record.getApprovalTime());
            item.put("approverId", record.getApproverId());
            item.put("approvalRemark", record.getApprovalRemark());
            
            // 添加审批人姓名
            if (record.getApproverId() != null) {
                User approver = userService.getUserById(record.getApproverId());
                item.put("approverName", approver.getRealName() != null ? approver.getRealName() : approver.getUsername());
            } else {
                item.put("approverName", null);
            }

            result.add(item);
        }

        return Map.of("ok", true, "data", result);
    }
}