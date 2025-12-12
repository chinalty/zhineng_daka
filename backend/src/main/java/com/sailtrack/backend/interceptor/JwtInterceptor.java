package com.sailtrack.backend.interceptor;

import com.sailtrack.backend.entity.User;
import com.sailtrack.backend.repository.UserRepository;
import com.sailtrack.backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {
    
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.getUsername(token);
                User user = userRepository.findByUsername(username).orElse(null);
                
                if (user != null) {
                    // 将用户信息存入请求属性，方便后续使用
                    request.setAttribute("userId", user.getId());
                    request.setAttribute("username", username);
                    request.setAttribute("user", user);
                    return true;
                }
            }
        }
        
        // 对于不需要认证的接口，直接放行
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/") || path.startsWith("/api/test/")) {
            return true;
        }
        
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"ok\":false,\"code\":401,\"message\":\"请先登录\"}");
        return false;
    }
}