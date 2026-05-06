package com.pos.platform.dto.mini;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 抽奖记录响应
 */
@Data
@Builder
public class LotteryRecordResponse {
    
    /** 记录ID */
    private Long id;
    
    /** 奖项名称 */
    private String prizeName;
    
    /** 奖励积分数 */
    private Integer prizePoints;
    
    /** 抽奖时间 */
    private LocalDateTime createdAt;
}
