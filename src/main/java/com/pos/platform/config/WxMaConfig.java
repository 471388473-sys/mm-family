package com.pos.platform.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaDefaultConfigImpl;
import cn.binarywang.wx.miniapp.util.WxMaConfigHolder;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;

/**
 * 微信小程序配置
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(WxMaProperties.class)
public class WxMaConfig {

    @Autowired
    private WxMaProperties properties;

    @Bean
    public WxMaService wxMaService() {
        if (properties.getConfigs() == null || properties.getConfigs().isEmpty()) {
            throw new WxRuntimeException("请先配置微信小程序参数");
        }

        WxMaService maService = new WxMaServiceImpl();
        maService.setMultiConfigs(
            properties.getConfigs().stream()
                .map(config -> {
                    WxMaDefaultConfigImpl wxMaConfig = new WxMaDefaultConfigImpl();
                    wxMaConfig.setAppid(config.getAppid());
                    wxMaConfig.setSecret(config.getSecret());
                    wxMaConfig.setToken(config.getToken());
                    wxMaConfig.setAesKey(config.getAesKey());
                    wxMaConfig.setMsgDataFormat(config.getMsgDataFormat());
                    return wxMaConfig;
                })
                .collect(Collectors.toMap(WxMaDefaultConfigImpl::getAppid, c -> c, (o, n) -> o))
        );
        return maService;
    }
}