package com.sailtrack.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;   // Spring 自动注入

    /**
     * 发送纯文本验证码
     * @param to    收件人
     * @param code  4 位数字验证码
     */
    public void sendCaptcha(String to, String code) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("tracksail@163.com");   // 发件人（必须和配置一致）
        msg.setTo(to);                      // 收件人
        msg.setSubject("SailTrack 注册验证码");
        msg.setText("验证码：" + code + "，5 分钟内有效，请勿泄露！");
        mailSender.send(msg);               // 真正发信
    }
}