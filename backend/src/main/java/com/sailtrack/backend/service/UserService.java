package com.sailtrack.backend.service;

import com.sailtrack.backend.dto.UserResponse;
import com.sailtrack.backend.entity.Department;
import com.sailtrack.backend.entity.Role;
import com.sailtrack.backend.entity.User;
import com.sailtrack.backend.repository.DepartmentRepository;
import com.sailtrack.backend.repository.RoleRepository;
import com.sailtrack.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    
    public UserResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRealName(user.getRealName());
        response.setDepartmentId(user.getDepartmentId());
        response.setRoleId(user.getRoleId());
        response.setStatus(user.getStatus());
        
        // 获取角色名称
        if (user.getRoleId() != null) {
            roleRepository.findById(user.getRoleId())
                    .ifPresent(role -> response.setRoleName(role.getRoleName()));
        }
        
        // 获取部门名称
        if (user.getDepartmentId() != null) {
            departmentRepository.findById(user.getDepartmentId())
                    .ifPresent(dept -> response.setDepartmentName(dept.getDepartmentName()));
        }
        
        // 状态文本
        response.setStatusText(user.getStatus() == 1 ? "启用" : "禁用");
        
        return response;
    }
    
    public List<UserResponse> getDepartmentUsers(Long managerId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("管理员不存在"));
        
        if (!manager.getRoleId().equals(2L)) {
            throw new RuntimeException("无权限查看部门员工");
        }
        
        List<User> users = userRepository.findByDepartmentIdAndStatus(manager.getDepartmentId(), 1);
        
        return users.stream().map(user -> {
            UserResponse response = new UserResponse();
            response.setId(user.getId());
            response.setUsername(user.getUsername());
            response.setEmail(user.getEmail());
            response.setRealName(user.getRealName());
            response.setDepartmentId(user.getDepartmentId());
            response.setRoleId(user.getRoleId());
            response.setStatus(user.getStatus());
            
            // 获取角色名称
            if (user.getRoleId() != null) {
                roleRepository.findById(user.getRoleId())
                        .ifPresent(role -> response.setRoleName(role.getRoleName()));
            }
            
            // 获取部门名称
            if (user.getDepartmentId() != null) {
                departmentRepository.findById(user.getDepartmentId())
                        .ifPresent(dept -> response.setDepartmentName(dept.getDepartmentName()));
            }
            
            response.setStatusText(user.getStatus() == 1 ? "启用" : "禁用");
            
            return response;
        }).collect(Collectors.toList());
    }
    
    public List<Department> getAllDepartments() {
        return departmentRepository.findByStatus(1);
    }
    
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}