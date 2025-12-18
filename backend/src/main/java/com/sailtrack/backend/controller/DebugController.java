package com.sailtrack.backend.controller;

import com.sailtrack.backend.cache.CaptchaCache;
import com.sailtrack.backend.service.MailService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {
    private final CaptchaCache captchaCache;
    private final MailService mailService;
    
    public DebugController(CaptchaCache captchaCache, MailService mailService) {
        this.captchaCache = captchaCache;
        this.mailService = mailService;
    }
    
    @PostMapping("/set-captcha")
    public Map<String, Object> setCaptcha(@RequestParam String email,
                                         @RequestParam String code) {
        String normalizedEmail = email.toLowerCase();
        captchaCache.save(normalizedEmail, code);
        return Map.of("ok", true, "message", "验证码已设置");
    }
    
    @PostMapping("/test-email")
    public Map<String, Object> testEmail(@RequestParam String email,
                                        @RequestParam String code) {
        String normalizedEmail = email.toLowerCase();
        mailService.sendCaptcha(normalizedEmail, code);
        return Map.of("ok", true, "message", "测试邮件已发送");
    }
    
    @GetMapping("/check-cache")
    public Map<String, Object> checkCache(@RequestParam String email) {
        String normalizedEmail = email.toLowerCase();
        String cachedCode = captchaCache.get(normalizedEmail);
        boolean exists = cachedCode != null;
        
        return Map.of("ok", true, "data", Map.of(
            "email", normalizedEmail,
            "exists", exists,
            "code", exists ? cachedCode : null
        ));
    }
}