package com.pos.platform.entity.activation;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 激活产品表
 */
@Data
@TableName("activation_product")
public class ActivationProduct implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 产品ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 产品名称 */
    private String name;

    /** 产品描述 */
    private String description;

    /** 排序值 */
    private Integer sort;

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