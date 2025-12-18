package com.sailtrack.backend.controller;

import com.sailtrack.backend.dto.ChangePasswordRequest;
import com.sailtrack.backend.dto.UpdateProfileRequest;
import com.sailtrack.backend.entity.Department;
import com.sailtrack.backend.entity.Role;
import com.sailtrack.backend.entity.User;
import com.sailtrack.backend.repository.DepartmentRepository;
import com.sailtrack.backend.repository.RoleRepository;
import com.sailtrack.backend.service.UserService;
import com.sailtrack.backend.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;
    private final com.sailtrack.backend.service.FaceRecognitionService faceRecognitionService;
    
    public UserController(UserService userService, DepartmentRepository departmentRepository,
                         RoleRepository roleRepository, JwtUtil jwtUtil,
                         com.sailtrack.backend.service.FaceRecognitionService faceRecognitionService) {
        this.userService = userService;
        this.departmentRepository = departmentRepository;
        this.roleRepository = roleRepository;
        this.jwtUtil = jwtUtil;
        this.faceRecognitionService = faceRecognitionService;
    }
    
    @GetMapping("/info")
    public Map<String, Object> getUserInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        
                
        
                User user = userService.getUserById(userId);
        
        // 获取部门信息
        String departmentName = "";
        if (user.getDepartmentId() != null) {
            Department department = departmentRepository.findById(user.getDepartmentId()).orElse(null);
            if (department != null) {
                departmentName = department.getName();
            }
        }
        
        // 获取角色信息
        String roleName = "";
        if (user.getRoleId() != null) {
            Role role = roleRepository.findById(user.getRoleId()).orElse(null);
            if (role != null) {
                roleName = role.getName();
            }
        }
        
        java.util.Map<String, Object> userData = new java.util.HashMap<>();
        userData.put("id", user.getId());
        userData.put("username", user.getUsername());
        userData.put("email", user.getEmail());
        userData.put("realName", user.getRealName() != null ? user.getRealName() : "");
        userData.put("departmentId", user.getDepartmentId());
        userData.put("departmentName", departmentName);
        userData.put("roleId", user.getRoleId());
        userData.put("roleName", roleName);
        userData.put("faceImageUrl", user.getFaceImageUrl());
        userData.put("status", user.getStatus());
        userData.put("createdAt", user.getCreatedAt());
        
        return Map.of("ok", true, "data", userData);
    }
    
    @PutMapping("/update-profile")
    public Map<String, Object> updateProfile(HttpServletRequest request,
                                            @Valid @RequestBody UpdateProfileRequest dto) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        userService.updateProfile(userId, dto);
        
        return Map.of("ok", true, "message", "个人信息更新成功");
    }
    
    @PutMapping("/change-password")
    public Map<String, Object> changePassword(HttpServletRequest request,
                                             @Valid @RequestBody ChangePasswordRequest dto) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        userService.changePassword(userId, dto);
        
        return Map.of("ok", true, "message", "密码修改成功");
    }
    
    @GetMapping("/stats")
    public Map<String, Object> getUserStats(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        var stats = userService.getUserStats(userId);
        
        return Map.of("ok", true, "data", stats);
    }
    
    @GetMapping("/department-users")
    public Map<String, Object> getDepartmentUsers(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userService.getUserById(userId);
        
        // 只有部门经理及以上角色可以查看部门员工
        if (user.getRoleId() > 2) {
            throw new RuntimeException("无权限查看部门员工");
        }
        
        List<User> users = userService.getDepartmentUsers(user.getDepartmentId());
        
        return Map.of("ok", true, "data", users);
    }
    
    @GetMapping("/departments")
    public Map<String, Object> getDepartments() {
        List<Department> departments = departmentRepository.findAllByOrderByNameAsc();
        return Map.of("ok", true, "data", departments);
    }
    
    @GetMapping("/roles")
    public Map<String, Object> getRoles() {
        List<Role> roles = roleRepository.findAll();
        return Map.of("ok", true, "data", roles);
    }
    
    @PostMapping("/upload-face")
    public Map<String, Object> uploadFaceImage(HttpServletRequest request,
                                               @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        if (!faceRecognitionService.isConfigured()) {
            throw new RuntimeException("人脸识别功能未配置，请联系管理员");
        }
        
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        try {
            String faceImageUrl = faceRecognitionService.uploadFaceImage(file, userId);
            userService.updateFaceImage(userId, faceImageUrl);
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("ok", true);
            response.put("message", "人脸照片上传成功");
            response.put("url", faceImageUrl);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("人脸照片上传失败: " + e.getMessage());
        }
    }
}