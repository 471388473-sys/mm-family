package com.pos.platform.dto.mini;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * POS机领取申请请求DTO
 */
@Data
public class PosApplyRequest {

    /** 申请人姓名 */
    @NotBlank(message = "姓名不能为空")
    private String name;

    /** 申请人手机号 */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /** 收货地址 */
    @NotBlank(message = "收货地址不能为空")
    private String address;

    /** 验证码 */
    @NotBlank(message = "验证码不能为空")
    private String code;
}
