package com.sailtrack.backend.service;

import com.sailtrack.backend.dto.LoginRequest;
import com.sailtrack.backend.dto.RegisterRequest;
import com.sailtrack.backend.entity.User;
import com.sailtrack.backend.repository.UserRepository;
import com.sailtrack.backend.util.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.sailtrack.backend.cache.CaptchaCache;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CaptchaCache captchaCache;
    
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                      JwtUtil jwtUtil, CaptchaCache captchaCache) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.captchaCache = captchaCache;
    }

    @Transactional
    public Long register(RegisterRequest dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (!captchaCache.verify(dto.getEmail().toLowerCase(), dto.getCaptcha())) {
            throw new RuntimeException("验证码错误");
        }
        if (userRepository.existsByEmail(dto.getEmail().toLowerCase())){
            throw new RuntimeException("邮箱已使用");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setDepartmentId(dto.getDepartmentId()); // 用户选择的部门
        user.setRoleId(3L); // 默认角色：普通员工
        user.setStatus(1); // 默认启用
        return userRepository.save(user).getId();
    }

    public String login(LoginRequest dto) {
        // 1. 查用户
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        // 2. 比密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        // 3. 生成 JWT
        return jwtUtil.generate(user.getUsername());
    }
}