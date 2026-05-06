package com.pos.platform.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 短信验证码日志表
 */
@Data
@TableName("sms_log")
public class SmsLog implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 日志ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 手机号 */
    private String phone;

    /** 验证码 */
    private String code;

    /** 场景：pos_apply/login/other */
    private String scene;

    /** 请求IP */
    private String ip;

    /** 状态：0-未使用 1-已使用 2-已过期 */
    private Integer status;

    /** 过期时间 */
    private LocalDateTime expireAt;

    /** 发送时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}