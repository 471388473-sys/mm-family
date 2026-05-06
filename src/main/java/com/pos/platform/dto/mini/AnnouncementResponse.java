package com.pos.platform.dto.mini;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 公告响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementResponse {

    /** 公告ID */
    private Long id;

    /** 标题 */
    private String title;

    /** 内容（富文本HTML） */
    private String content;

    /** 摘要 */
    private String summary;

    /** 类型：important/notice/activity */
    private String type;

    /** 类型描述 */
    private String typeDesc;

    /** 是否置顶：0-否 1-是 */
    private Integer isTop;

    /** 发布时间 */
    private LocalDateTime publishedAt;
}
