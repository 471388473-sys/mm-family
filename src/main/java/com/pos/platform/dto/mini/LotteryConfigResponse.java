package com.pos.platform.dto.mini;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 抽奖配置响应
 */
@Data
@Builder
public class LotteryConfigResponse {
    
    /** 活动是否开启 */
    private Boolean enabled;
    
    /** 活动名称 */
    private String title;
    
    /** 抽奖阈值（积分） */
    private Integer threshold;
    
    /** 每日抽奖次数上限 */
    private Integer dailyLimit;
    
    /** 今日剩余抽奖次数 */
    private Integer remainingChances;
    
    /** 奖项列表 */
    private List<PrizeResponse> prizes;
    
    /**
     * 奖项响应
     */
    @Data
    @Builder
    public static class PrizeResponse {
        /** 奖项ID */
        private Long id;
        /** 奖项名称 */
        private String name;
        /** 奖励积分数 */
        private Integer points;
        /** 中奖概率 */
        private BigDecimal probability;
    }
}
