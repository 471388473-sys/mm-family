package com.pos.platform.dto.mini;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 小程序登录响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /** 用户ID */
    private Long userId;

    /** Token */
    private String token;

    /** 手机号 */
    private String phone;

    /** 昵称 */
    private String nickname;

    /** 头像 */
    private String avatar;

    /** 是否新用户 */
    private Boolean isNewUser;
}
