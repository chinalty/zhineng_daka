package com.sailtrack.backend.controller;

import com.sailtrack.backend.cache.CaptchaCache;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {
    private final CaptchaCache captchaCache;
    
    public TestController(CaptchaCache captchaCache) {
        this.captchaCache = captchaCache;
    }
    
    @GetMapping("/captcha-cache")
    public Map<String, Object> getCaptchaCache() {
        return Map.of("ok", true, "data", captchaCache.getAll());
    }
    
    @PostMapping("/test-captcha")
    public Map<String, Object> testCaptcha(@RequestParam String email,
                                          @RequestParam String code) {
        boolean isValid = captchaCache.verify(email.toLowerCase(), code);
        return Map.of("ok", true, "data", Map.of("valid", isValid));
    }
    
    @GetMapping("/token-info")
    public Map<String, Object> getTokenInfo(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return Map.of("ok", true, "data", Map.of(
                "tokenReceived", true,
                "tokenLength", token.length(),
                "tokenPreview", token.substring(0, Math.min(50, token.length())) + "..."
            ));
        } else {
            return Map.of("ok", true, "data", Map.of(
                "tokenReceived", false,
                "message", "没有收到有效的Authorization头"
            ));
        }
    }
}