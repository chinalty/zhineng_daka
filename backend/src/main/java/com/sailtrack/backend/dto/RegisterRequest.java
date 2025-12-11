package com.sailtrack.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank @Size(min=3,max=20)
    private String username;

    @NotBlank @Size(min=8,max=30)
    private String password;

    @NotBlank @Email
    private String email;

    @NotBlank @Size(min=4,max=6)
    private String captcha;
    // getter/setter 省略
}