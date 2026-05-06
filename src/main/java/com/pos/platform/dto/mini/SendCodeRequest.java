package com.pos.platform.dto.mini;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 发送验证码请求DTO
 */
@Data
public class SendCodeRequest {

    /** 手机号 */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /** 场景：pos_apply-申请POS机, login-登录, other-其他 */
    @NotBlank(message = "场景不能为空")
    private String scene;
}
