package com.pos.platform.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理员信息响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminInfoResponse implements Serializable {
    private static final long serialVersionUID = 1L;

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

    /** 最后登录时间 */
    private LocalDateTime lastLoginAt;
}
