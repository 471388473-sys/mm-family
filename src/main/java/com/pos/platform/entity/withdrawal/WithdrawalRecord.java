package com.pos.platform.entity.withdrawal;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现记录表
 */
@Data
@TableName("withdrawal_record")
public class WithdrawalRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 提现记录ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 提现金额（元） */
    private BigDecimal amount;

    /** 固定手续费（元） */
    private BigDecimal fixedFee;

    /** 提现时费率 */
    private BigDecimal feeRate;

    /** 费率计算费用（元） */
    private BigDecimal rateFee;

    /** 总费用（元） */
    private BigDecimal totalFee;

    /** 实际到账金额（元） */
    private BigDecimal actualAmount;

    /** 状态：0-待审核 1-审核通过 2-审核拒绝 3-已打款 */
    private Integer status;

    /** 拒绝原因 */
    private String rejectReason;

    /** 打款交易号 */
    private String transactionNo;

    /** 审核人ID */
    private Long approvedBy;

    /** 审核时间 */
    private LocalDateTime approvedAt;

    /** 打款确认人ID */
    private Long paidBy;

    /** 打款时间 */
    private LocalDateTime paidAt;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}