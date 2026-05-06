package com.pos.platform.entity.machine;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 机具交易量表
 */
@Data
@TableName("machine_transaction")
public class MachineTransaction implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 机具ID */
    private Long machineId;

    /** 机具编码（冗余） */
    private String machineCode;

    /** 本次导入交易量（元） */
    private BigDecimal transactionAmount;

    /** 导入后累计交易量（元） */
    private BigDecimal totalTransaction;

    /** 本次获得积分 */
    private Integer pointsEarned;

    /** 导入批次号 */
    private String importBatch;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}