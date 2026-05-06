package com.pos.platform.dto.mini;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 常见问题响应
 */
@Data
@Builder
public class FaqResponse {
    
    /** FAQ ID */
    private Long id;
    
    /** 问题 */
    private String question;
    
    /** 回答 */
    private String answer;
    
    /** 排序值 */
    private Integer sort;
}
