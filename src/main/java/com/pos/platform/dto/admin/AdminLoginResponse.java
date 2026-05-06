package com.pos.platform.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 管理端登录响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 访问令牌 */
    private String token;

    /** 管理员ID */
    private Long adminId;

    /** 用户名 */
    private String username;

    /** 真实姓名 */
    private String realName;

    /** 角色 */
    private String role;

    /** 角色名称 */
    private String roleName;

    /** 过期时间(秒) */
    private Long expireIn;
}
