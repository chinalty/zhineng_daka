package com.sailtrack.backend.controller;


import com.sailtrack.backend.cache.CaptchaCache;
import com.sailtrack.backend.dto.LoginRequest;
import com.sailtrack.backend.dto.RegisterRequest;
import com.sailtrack.backend.service.AuthService;
import com.sailtrack.backend.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

public class AuthController {
    private final AuthService authService;
    private final MailService mailService;
    private final CaptchaCache captchaCache;
    // 暂时没依赖，先空着，后面注入 AuthService

    //    邮箱验证
    @PostMapping("/send-captcha")
    public Map<String, Object> sendCaptcha(@RequestParam String email){
        String code = String.format("%04d", new Random().nextInt(10000));
        mailService.sendCaptcha(email, code);
        captchaCache.save(email, code);
        return Map.of("ok", true, "message", "验证码已发送");
    }
    //    注册接口
    @PostMapping("/register")
    public Map<String,Object> register(@Valid @RequestBody RegisterRequest dto) {
        Long userId = authService.register(dto); // ② 一行调 Service
        return Map.of("ok", true, "code", HttpStatus.BAD_REQUEST.value(),"userId", userId);
    }

    //    登录接口
    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginRequest dto) {
        String token = authService.login(dto);
        return Map.of("ok", true, "code", 200, "token", token);
    }
}
