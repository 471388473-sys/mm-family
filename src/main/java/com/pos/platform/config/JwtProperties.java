package com.pos.platform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * JWT配置属性
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    
    /** JWT签名密钥 */
    private String secret;
    
    /** 小程序Token有效期(毫秒)，默认7天 */
    private Long miniExpireTime = 604800000L;
    
    /** 管理端Token有效期(毫秒)，默认2小时 */
    private Long adminExpireTime = 7200000L;
}