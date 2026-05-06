package com.pos.platform.dto.mini;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 提现请求
 */
@Data
public class WithdrawalRequest {
    
    @NotNull(message = "提现金额不能为空")
    @DecimalMin(value = "0.01", message = "提现金额必须大于0")
    private BigDecimal amount;
}
