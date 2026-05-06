package com.pos.platform.entity.points;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 积分变动记录表
 */
@Data
@TableName("points_record")
public class PointsRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 类型：import/exchange/lottery/adjust */
    private String type;

    /** 积分变动值 */
    private Integer pointsChange;

    /** 余额变动值 */
    private BigDecimal balanceChange;

    /** 变动后积分余额 */
    private Integer pointsAfter;

    /** 变动后余额 */
    private BigDecimal balanceAfter;

    /** 变动描述 */
    private String description;

    /** 关联业务ID */
    private Long refId;

    /** 关联业务类型 */
    private String refType;

    /** 操作人ID，系统自动则为null */
    private Long operatorId;

    /** 变动时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}