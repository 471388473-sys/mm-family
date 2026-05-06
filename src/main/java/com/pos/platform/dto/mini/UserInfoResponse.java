package com.pos.platform.dto.mini;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    /** 用户ID */
    private Long userId;

    /** 手机号 */
    private String phone;

    /** 昵称 */
    private String nickname;

    /** 头像 */
    private String avatar;

    /** 状态 */
    private Integer status;

    /** 最后登录时间 */
    private String lastLoginAt;
}
