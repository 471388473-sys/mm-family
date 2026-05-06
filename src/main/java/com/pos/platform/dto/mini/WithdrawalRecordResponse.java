package com.pos.platform.dto.mini;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现记录响应
 */
@Data
@Builder
public class WithdrawalRecordResponse {
    
    /** 提现记录ID */
    private Long id;
    
    /** 提现金额 */
    private BigDecimal amount;
    
    /** 手续费 */
    private BigDecimal totalFee;
    
    /** 实际到账金额 */
    private BigDecimal actualAmount;
    
    /** 状态：0-待审核 1-审核通过 2-审核拒绝 3-已打款 */
    private Integer status;
    
    /** 状态描述 */
    private String statusText;
    
    /** 拒绝原因 */
    private String rejectReason;
    
    /** 申请时间 */
    private LocalDateTime createdAt;
    
    /** 审核时间 */
    private LocalDateTime approvedAt;
    
    /** 打款时间 */
    private LocalDateTime paidAt;
}
