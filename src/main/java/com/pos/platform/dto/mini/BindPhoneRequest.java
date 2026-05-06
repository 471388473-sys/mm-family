package com.pos.platform.dto.mini;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 绑定手机号请求DTO
 */
@Data
public class BindPhoneRequest {

    /** 微信加密手机号 */
    @NotBlank(message = "加密手机号不能为空")
    private String phone;

    /** 验证码 */
    @NotBlank(message = "验证码不能为空")
    private String code;

    /** 加密向量 */
    private String iv;
}
