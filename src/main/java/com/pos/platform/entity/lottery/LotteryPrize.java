package com.pos.platform.entity.lottery;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 抽奖奖项表
 */
@Data
@TableName("lottery_prize")
public class LotteryPrize implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 奖项ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 奖项名称，如"一等奖" */
    private String name;

    /** 奖励积分数 */
    private Integer points;

    /** 中奖概率，0-1之间 */
    private BigDecimal probability;

    /** 奖品总量，-1表示不限量 */
    private Integer totalCount;

    /** 剩余数量，-1表示不限量 */
    private Integer remainingCount;

    /** 排序值，升序 */
    private Integer sort;

    /** 是否启用：0-禁用 1-启用 */
    private Integer enabled;

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