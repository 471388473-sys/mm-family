package com.pos.platform.entity.lottery;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 用户每日抽奖统计表
 */
@Data
@TableName("lottery_daily")
public class LotteryDaily implements Serializable {
    private static final long serialVersionUID = 1L;

    /** ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 日期 */
    private LocalDate date;

    /** 当日已抽次数 */
    private Integer drawCount;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private java.time.LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private java.time.LocalDateTime updatedAt;
}