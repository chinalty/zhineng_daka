package com.sailtrack.backend.controller;

import com.sailtrack.backend.cache.CaptchaCache;
import com.sailtrack.backend.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
public class DebugController {
    
    private final CaptchaCache captchaCache;
    private final MailService mailService;
    
    @PostMapping("/set-captcha")
    public Map<String, Object> setCaptcha(@RequestParam String email, 
                                          @RequestParam String code) {
        captchaCache.save(email.toLowerCase(), code);
        return Map.of(
            "ok", true,
            "message", "验证码已设置",
            "email", email.toLowerCase(),
            "code", code
        );
    }
    
    @PostMapping("/test-email")
    public Map<String, Object> testEmail(@RequestParam String email, 
                                        @RequestParam String code) {
        try {
            mailService.sendCaptcha(email.toLowerCase(), code);
            return Map.of("ok", true, "message", "邮件发送成功");
        } catch (Exception e) {
            return Map.of(
                "ok", false,
                "message", "邮件发送失败",
                "error", e.getMessage()
            );
        }
    }
    
    @GetMapping("/check-cache")
    public Map<String, Object> checkCache(@RequestParam String email) {
        String normalizedEmail = email.toLowerCase();
        boolean exists = captchaCache.getAll().containsKey(normalizedEmail);
        String code = captchaCache.getAll().get(normalizedEmail);
        
        return Map.of(
            "ok", true,
            "email", normalizedEmail,
            "exists", exists,
            "code", code
        );
    }
}