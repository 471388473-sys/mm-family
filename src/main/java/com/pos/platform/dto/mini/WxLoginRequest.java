package com.pos.platform.dto.mini;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 微信登录请求DTO
 */
@Data
public class WxLoginRequest {

    /** 微信登录code */
    @NotBlank(message = "code不能为空")
    private String code;

    /** 昵称 */
    private String nickname;

    /** 头像URL */
    private String avatar;
}
