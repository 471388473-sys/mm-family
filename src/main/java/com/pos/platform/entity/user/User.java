package com.pos.platform.entity.user;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 客户用户表
 */
@Data
@TableName("user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 微信openid */
    private String openid;

    /** 微信unionid */
    private String unionId;

    /** 手机号 */
    private String phone;

    /** 昵称 */
    private String nickname;

    /** 头像URL */
    private String avatar;

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