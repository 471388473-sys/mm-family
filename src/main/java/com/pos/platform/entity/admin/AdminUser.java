package com.pos.platform.entity.admin;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理员表
 */
@Data
@TableName("admin_user")
public class AdminUser implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 管理员ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户名 */
    private String username;

    /** 密码（bcrypt加密存储） */
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 角色：admin/service */
    private String role;

    /** 状态：1-正常 0-禁用 */
    private Integer status;

    /** 最后登录时间 */
    private LocalDateTime lastLoginAt;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 软删除时间 */
    @TableField(fill = FieldFill.INSERT)
    @TableLogic
    private LocalDateTime deletedAt;
}