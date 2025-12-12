package com.sailtrack.backend.cache;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

@Component
public class CaptchaCache {

    private static final Logger logger = LoggerFactory.getLogger(CaptchaCache.class);
    private final Map<String, String> store = new ConcurrentHashMap<>();

    // 5 分钟后自动删除
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    /**
     * 保存验证码
     * @param email 邮箱作 key
     * @param code  4 位数字
     */
    public void save(String email, String code) {
        String normalizedEmail = email.toLowerCase(); // 统一转小写
        store.put(normalizedEmail, code);
        logger.info("验证码已保存: 邮箱={}, 验证码={}", normalizedEmail, code);
        // 5 分钟删
        scheduler.schedule(() -> {
            store.remove(normalizedEmail);
            logger.info("验证码已过期: 邮箱={}", normalizedEmail);
        }, 5, TimeUnit.MINUTES);
    }

    /**
     * 验证验证码
     * @return true=正确
     */
    public boolean verify(String email, String code) {
        String normalizedEmail = email.toLowerCase(); // 统一转小写
        String cachedCode = store.get(normalizedEmail);
        logger.info("验证验证码: 邮箱={}, 输入={}, 缓存={}", normalizedEmail, code, cachedCode);
        
        if (cachedCode != null && cachedCode.equals(code)) {
            // 验证成功后删除验证码，防止重复使用
            store.remove(normalizedEmail);
            logger.info("验证码验证成功: 邮箱={}", normalizedEmail);
            return true;
        }
        logger.warn("验证码验证失败: 邮箱={}", normalizedEmail);
        return false;
    }
    
    /**
     * 获取当前缓存的所有验证码（调试用）
     */
    public Map<String, String> getAll() {
        return store;
    }
}
