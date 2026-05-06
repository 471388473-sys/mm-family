package com.pos.platform.entity.activation;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 激活步骤表
 */
@Data
@TableName("activation_step")
public class ActivationStep implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 步骤ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 产品ID */
    private Long productId;

    /** 步骤标题 */
    private String title;

    /** 步骤说明 */
    private String description;

    /** 引导图片URL */
    private String imageUrl;

    /** 排序值，升序 */
    private Integer sort;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}