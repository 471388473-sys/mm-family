package com.pos.platform.service.mini;

import com.pos.platform.dto.mini.SendCodeRequest;
import com.pos.platform.dto.mini.SendCodeResponse;

/**
 * 短信服务接口
 */
public interface SmsService {

    /**
     * 发送验证码
     * @param request 发送请求
     * @param ip IP地址
     * @return 发送结果
     */
    SendCodeResponse sendCode(SendCodeRequest request, String ip);

    /**
     * 验证验证码
     * @param phone 手机号
     * @param code 验证码
     * @param scene 场景
     * @return true-验证通过
     */
    boolean verifyCode(String phone, String code, String scene);

    /**
     * 验证并消耗验证码（验证后标记为已使用）
     * @param phone 手机号
     * @param code 验证码
     * @param scene 场景
     * @return true-验证通过
     */
    boolean verifyAndConsumeCode(String phone, String code, String scene);
}
