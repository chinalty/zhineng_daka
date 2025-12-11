package com.sailtrack.backend.controller;

import com.sailtrack.backend.repository.UserRepository;   // ← 引入顶级接口
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private UserRepository userRepository;   // 使用独立的顶级接口

    @GetMapping("/db")
    public Map<String, Long> db() {
        return Map.of("count", userRepository.count());
    }
}