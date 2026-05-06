package com.pos.platform.dto.admin;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 管理端登录请求
 */
@Data
public class AdminLoginRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    /** 滑动验证码token */
    private String captchaToken;

    /** 滑动验证码轨迹 */
    private String captchaTrack;
}
