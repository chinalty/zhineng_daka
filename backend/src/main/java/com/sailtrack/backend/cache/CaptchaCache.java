package com.sailtrack.backend.cache;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

@Component
public class CaptchaCache {

    private final Map<String, String> store = new ConcurrentHashMap<>();

    // 5 分钟后自动删除
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    /**
     * 保存验证码
     * @param email 邮箱作 key
     * @param code  4 位数字
     */
    public void save(String email, String code) {
        store.put(email, code);
        // 5 分钟删
        scheduler.schedule(() -> store.remove(email), 5, TimeUnit.MINUTES);
    }

    /**
     * 验证验证码
     * @return true=正确
     */
    public boolean verify(String email, String code) {
        return code.equals(store.get(email));
    }
}
