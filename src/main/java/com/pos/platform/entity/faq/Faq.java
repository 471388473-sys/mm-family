package com.pos.platform.entity.faq;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 常见问题表
 */
@Data
@TableName("faq")
public class Faq implements Serializable {
    private static final long serialVersionUID = 1L;

    /** FAQ ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 问题 */
    private String question;

    /** 回答 */
    private String answer;

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