package com.sailtrack.backend;

// import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.TimeZone;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        // 手动加载 .env 文件（不依赖 dotenv-java 库）
        loadEnvFile();
        
        // 设置应用默认时区为上海时区
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(BackendApplication.class, args);
    }
    
    private static void loadEnvFile() {
        // 尝试多个可能的 .env 文件位置
        File envFile = null;
        String[] possiblePaths = {
            ".env",                           // 当前目录
            "backend/.env",                   // 从项目根目录
            "../.env",                        // 父目录
            System.getProperty("user.dir") + "/.env",  // 工作目录
            System.getProperty("user.dir") + "/backend/.env"  // 工作目录下的 backend
        };
        
        for (String path : possiblePaths) {
            File file = new File(path);
            if (file.exists()) {
                envFile = file;
                System.out.println("找到 .env 文件: " + file.getAbsolutePath());
                break;
            }
        }
        
        if (envFile == null) {
            System.out.println("⚠ 未找到 .env 文件，将使用系统环境变量或默认值");
            System.out.println("当前工作目录: " + System.getProperty("user.dir"));
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // 跳过空行和注释
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                // 解析 KEY=VALUE 格式
                int equalsIndex = line.indexOf('=');
                if (equalsIndex > 0) {
                    String key = line.substring(0, equalsIndex).trim();
                    String value = line.substring(equalsIndex + 1).trim();
                    System.setProperty(key, value);
                    count++;
                    
                    // 隐藏敏感信息
                    String displayValue = (key.contains("PASSWORD") || key.contains("SECRET")) ? "******" : value;
                    System.out.println("加载环境变量: " + key + " = " + displayValue);
                }
            }
            System.out.println("✓ 成功加载 " + count + " 个环境变量");
        } catch (Exception e) {
            System.out.println("⚠ 加载 .env 文件失败: " + e.getMessage());
        }
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}