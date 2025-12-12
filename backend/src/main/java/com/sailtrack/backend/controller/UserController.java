package com.sailtrack.backend.controller;

import com.sailtrack.backend.dto.UserResponse;
import com.sailtrack.backend.entity.Department;
import com.sailtrack.backend.entity.Role;
import com.sailtrack.backend.service.UserService;
import com.sailtrack.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    @GetMapping("/info")
    public Map<String, Object> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("请先登录");
        }
        
        UserResponse userInfo = userService.getUserInfo(userId);
        return Map.of("ok", true, "data", userInfo);
    }
    
    @GetMapping("/department-users")
    public Map<String, Object> getDepartmentUsers(HttpServletRequest request) {
        Long managerId = (Long) request.getAttribute("userId");
        if (managerId == null) {
            throw new RuntimeException("请先登录");
        }
        
        List<UserResponse> users = userService.getDepartmentUsers(managerId);
        return Map.of("ok", true, "data", users);
    }
    
    @GetMapping("/departments")
    public Map<String, Object> getAllDepartments() {
        List<Department> departments = userService.getAllDepartments();
        return Map.of("ok", true, "data", departments);
    }
    
    @GetMapping("/roles")
    public Map<String, Object> getAllRoles() {
        List<Role> roles = userService.getAllRoles();
        return Map.of("ok", true, "data", roles);
    }
}