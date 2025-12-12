package com.sailtrack.backend.controller;

import com.sailtrack.backend.cache.CaptchaCache;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    
    private final CaptchaCache captchaCache;
    
    @GetMapping("/captcha-cache")
    public Map<String, Object> getCaptchaCache() {
        return Map.of(
            "ok", true,
            "data", captchaCache.getAll(),
            "size", captchaCache.getAll().size()
        );
    }
    
    @PostMapping("/test-captcha")
    public Map<String, Object> testCaptcha(@RequestParam String email, 
                                          @RequestParam String code) {
        boolean result = captchaCache.verify(email, code);
        return Map.of(
            "ok", true,
            "email", email,
            "code", code,
            "result", result
        );
    }
}