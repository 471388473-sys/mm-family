package com.pos.platform.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 积分账户响应DTO
 */
@Data
public class PointsAccountResponse {
    
    /** 账户ID */
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 当前积分总额 */
    private Integer points;
    
    /** 当前余额（元） */
    private BigDecimal balance;
    
    /** 累计兑换金额（元） */
    private BigDecimal totalExchanged;
    
    /** 累计提现金额（元） */
    private BigDecimal totalWithdrawn;
    
    /** 累计抽奖获得积分 */
    private Integer totalLotteryWinnings;
    
    /** 账户创建时间 */
    private LocalDateTime createdAt;
}
