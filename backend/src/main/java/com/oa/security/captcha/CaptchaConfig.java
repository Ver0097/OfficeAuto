package com.oa.security.captcha;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 验证码配置
 */
@Configuration
public class CaptchaConfig {

    @Bean
    public DefaultKaptcha captchaProducer() {
        Properties properties = new Properties();
        // 图片边框
        properties.setProperty("kaptcha.border", "no");
        // 图片宽度
        properties.setProperty("kaptcha.image.width", "120");
        // 图片高度
        properties.setProperty("kaptcha.image.height", "40");
        // 验证码字符数
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        // 验证码字符范围（排除易混淆字符）
        properties.setProperty("kaptcha.textproducer.char.string", "ABCDEFGHJKLMNPQRSTUVWXYZ23456789");
        // 字体大小
        properties.setProperty("kaptcha.textproducer.font.size", "32");
        // 字体颜色
        properties.setProperty("kaptcha.textproducer.font.color", "black");
        // 字体样式
        properties.setProperty("kaptcha.textproducer.font.names", "Arial,Courier");
        // 干扰线颜色
        properties.setProperty("kaptcha.noise.color", "gray");
        // 背景颜色渐变
        properties.setProperty("kaptcha.background.clear.from", "white");
        properties.setProperty("kaptcha.background.clear.to", "white");

        Config config = new Config(properties);
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        kaptcha.setConfig(config);
        return kaptcha;
    }
}