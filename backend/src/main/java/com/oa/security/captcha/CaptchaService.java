package com.oa.security.captcha;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.oa.common.exception.BusinessException;
import com.oa.common.result.ResultCode;
import com.oa.security.dto.CaptchaVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final DefaultKaptcha captchaProducer;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 验证码缓存 Key 前缀
     */
    private static final String CAPTCHA_KEY_PREFIX = "captcha:";

    /**
     * 验证码过期时间（秒）
     */
    @Value("${captcha.expiration:300}")
    private Integer captchaExpiration;

    /**
     * 生成验证码
     *
     * @return 验证码响应 VO
     */
    public CaptchaVO generateCaptcha() {
        // 生成唯一标识
        String captchaKey = UUID.randomUUID().toString();

        // 生成验证码文本
        String captchaText = captchaProducer.createText();

        // 生成验证码图片
        BufferedImage image = captchaProducer.createImage(captchaText);

        // 将图片转为 Base64
        String captchaImage = imageToBase64(image);

        // 将验证码存入 Redis（5分钟过期）
        String redisKey = CAPTCHA_KEY_PREFIX + captchaKey;
        redisTemplate.opsForValue().set(redisKey, captchaText, captchaExpiration, TimeUnit.SECONDS);

        log.debug("生成验证码: key={}", captchaKey);

        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCaptchaKey(captchaKey);
        captchaVO.setCaptchaImage(captchaImage);
        return captchaVO;
    }

    /**
     * 验证验证码
     *
     * @param captchaKey  验证码唯一标识
     * @param captchaCode 用户输入的验证码
     * @return 是否验证成功
     */
    public boolean validateCaptcha(String captchaKey, String captchaCode) {
        if (captchaKey == null || captchaCode == null) {
            return false;
        }

        String redisKey = CAPTCHA_KEY_PREFIX + captchaKey;
        Object storedCode = redisTemplate.opsForValue().get(redisKey);

        if (storedCode == null) {
            throw new BusinessException(ResultCode.CAPTCHA_EXPIRED);
        }

        // 删除已使用的验证码（一次性使用）
        redisTemplate.delete(redisKey);

        // 验证码忽略大小写
        return storedCode.toString().equalsIgnoreCase(captchaCode);
    }

    /**
     * 图片转 Base64
     *
     * @param image 图片
     * @return Base64 字符串（含 data:image/png;base64, 前缀）
     */
    private String imageToBase64(BufferedImage image) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            byte[] bytes = outputStream.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            log.error("图片转 Base64 失败", e);
            throw new BusinessException("验证码生成失败");
        }
    }
}