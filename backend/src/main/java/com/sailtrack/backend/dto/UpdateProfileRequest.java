package com.sailtrack.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {
    @Size(min = 3, max = 30, message = "用户名长度在3到30个字符")
    private String username;
    
    @Size(min = 2, max = 50, message = "姓名长度在2到50个字符")
    private String realName;
    
    @Email(message = "请输入正确的邮箱地址")
    private String email;
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getRealName() {
        return realName;
    }
    
    public void setRealName(String realName) {
        this.realName = realName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
}