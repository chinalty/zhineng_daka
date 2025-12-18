package com.sailtrack.backend.dto;

public class UserResponse {
    // Getters and Setters
    private Long id;
    private String username;
    private String email;
    private String realName;
    private Long departmentId;
    private String departmentName;
    private Long roleId;
    private String roleName;
    private Integer status;
    private String statusText;

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }
}