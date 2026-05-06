package com.pos.platform.entity.pos;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * POS机领取申请表
 */
@Data
@TableName("pos_application")
public class PosApplication implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 申请ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 申请用户ID */
    private Long userId;

    /** 申请人姓名 */
    private String name;

    /** 申请人手机号 */
    private String phone;

    /** 收货地址 */
    private String address;

    /** 状态：1-已提交 2-已处理 */
    private Integer status;

    /** 处理人ID */
    private Long processedBy;

    /** 处理时间 */
    private LocalDateTime processedAt;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}