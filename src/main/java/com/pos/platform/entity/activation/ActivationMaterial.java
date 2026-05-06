package com.pos.platform.entity.activation;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 激活所需材料表
 */
@Data
@TableName("activation_material")
public class ActivationMaterial implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 材料ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 产品ID */
    private Long productId;

    /** 材料名称 */
    private String name;

    /** 是否必填：0-选填 1-必填 */
    private Integer required;

    /** 排序值 */
    private Integer sort;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}