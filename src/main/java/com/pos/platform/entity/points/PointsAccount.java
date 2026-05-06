package com.pos.platform.entity.points;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 积分账户表
 */
@Data
@TableName("points_account")
public class PointsAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 账户ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 当前积分总额 */
    private Integer points;

    /** 当前余额（元） */
    private BigDecimal balance;

    /** 累计兑换金额（元） */
    private BigDecimal totalExchanged;

    /** 累计提现金额（元） */
    private BigDecimal totalWithdrawn;

    /** 累计抽奖获得积分 */
    private Integer totalLotteryWinnings;

    /** 乐观锁版本号 */
    private Integer version;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}