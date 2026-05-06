package com.pos.platform.entity.lottery;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 抽奖活动配置表
 */
@Data
@TableName("lottery_config")
public class LotteryConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 配置ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 活动开关：0-关闭 1-开启 */
    private Integer enabled;

    /** 活动名称 */
    private String title;

    /** 抽奖阈值（积分） */
    private Integer threshold;

    /** 每人每日抽奖上限 */
    private Integer dailyLimit;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}