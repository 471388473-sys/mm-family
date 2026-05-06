package com.pos.platform.entity.machine;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 机具表
 */
@Data
@TableName("machine")
public class Machine implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 机具ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 机具编码（SN码） */
    private String machineCode;

    /** 产品ID */
    private Long productId;

    /** 绑定客户ID */
    private Long customerId;

    /** 绑定手机号（冗余字段） */
    private String bindPhone;

    /** 状态：0-未绑定 1-已绑定 */
    private Integer status;

    /** 绑定时间 */
    private LocalDateTime bindAt;

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