package com.pos.platform.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 导入批次表
 */
@Data
@TableName("import_batch")
public class ImportBatch implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 批次ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 批次号 */
    private String batchNo;

    /** 导入类型：machine/transaction */
    private String type;

    /** 导入总行数 */
    private Integer totalRows;

    /** 成功行数 */
    private Integer successRows;

    /** 失败行数 */
    private Integer failRows;

    /** 状态：0-处理中 1-完成 2-失败 */
    private Integer status;

    /** 失败明细，JSON格式 */
    private String errorDetail;

    /** 操作人ID */
    private Long operatorId;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}