package com.pos.platform.dto.mini;

import lombok.Builder;
import lombok.Data;

/**
 * 抽奖结果响应
 */
@Data
@Builder
public class LotteryResultResponse {
    
    /** 是否中奖 */
    private Boolean won;
    
    /** 奖项ID（未中奖为null） */
    private Long prizeId;
    
    /** 奖项名称 */
    private String prizeName;
    
    /** 奖励积分数 */
    private Integer prizePoints;
    
    /** 当前积分 */
    private Integer currentPoints;
    
    /** 剩余抽奖次数 */
    private Integer remainingChances;
    
    /** 抽奖前积分 */
    private Integer pointsBefore;
    
    /** 抽奖后积分 */
    private Integer pointsAfter;
}
