package com.pos.platform.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志表
 */
@Data
@TableName("operation_log")
public class OperationLog implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 日志ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 操作人ID */
    private Long operatorId;

    /** 操作人姓名 */
    private String operatorName;

    /** 模块：customer/machine/points/withdrawal/announcement/lottery/activation/faq/config */
    private String module;

    /** 操作：create/update/delete/import/export/approve/reject */
    private String action;

    /** 操作对象ID */
    private Long targetId;

    /** 操作详情，JSON格式 */
    private String detail;

    /** 操作IP */
    private String ip;

    /** 操作时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}