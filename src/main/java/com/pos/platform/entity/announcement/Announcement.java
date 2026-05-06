package com.pos.platform.entity.announcement;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公告表
 */
@Data
@TableName("announcement")
public class Announcement implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 公告ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 标题 */
    private String title;

    /** 内容（富文本HTML） */
    private String content;

    /** 摘要 */
    private String summary;

    /** 类型：important/notice/activity */
    private String type;

    /** 状态：0-草稿 1-已发布 */
    private Integer status;

    /** 是否置顶：0-否 1-是 */
    private Integer isTop;

    /** 发布时间 */
    private LocalDateTime publishedAt;

    /** 创建人ID */
    private Long createdBy;

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