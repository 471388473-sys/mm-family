package com.pos.platform.entity.lottery;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 抽奖记录表
 */
@Data
@TableName("lottery_record")
public class LotteryRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 抽奖记录ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 中奖奖项ID */
    private Long prizeId;

    /** 奖项名称（冗余） */
    private String prizeName;

    /** 奖励积分数 */
    private Integer prizePoints;

    /** 抽奖前积分 */
    private Integer pointsBefore;

    /** 抽奖后积分 */
    private Integer pointsAfter;

    /** 抽奖时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}