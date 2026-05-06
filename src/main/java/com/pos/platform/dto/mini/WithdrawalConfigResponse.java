package com.pos.platform.dto.mini;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 提现配置响应
 */
@Data
@Builder
public class WithdrawalConfigResponse {
    
    /** 最低提现金额 */
    private BigDecimal minAmount;
    
    /** 最高提现金额 */
    private BigDecimal maxAmount;
    
    /** 单笔固定手续费 */
    private BigDecimal fixedFee;
    
    /** 费率（百分比） */
    private BigDecimal feeRate;
    
    /** 是否启用费率 */
    private Boolean feeEnabled;
}
