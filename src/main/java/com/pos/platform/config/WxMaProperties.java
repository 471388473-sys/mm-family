package com.pos.platform.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 微信小程序配置属性
 */
@Data
@ConfigurationProperties(prefix = "wx.miniapp")
public class WxMaProperties {

    private List<Config> configs;

    @Data
    public static class Config {
        /** 微信小程序的appid */
        private String appid;
        
        /** 微信小程序的Secret */
        private String secret;
        
        /** 消息服务器配置的token */
        private String token;
        
        /** 消息服务器配置的EncodingAESKey */
        private String aesKey;
        
        /** 消息格式，XML或者JSON */
        private String msgDataFormat;
    }
}