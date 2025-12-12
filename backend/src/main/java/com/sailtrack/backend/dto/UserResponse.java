package com.sailtrack.backend.dto;

import lombok.Data;

@Data
public class UserResponse {
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
}