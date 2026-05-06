package com.pos.platform.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 积分记录响应DTO
 */
@Data
public class PointsRecordResponse {
    
    /** 记录ID */
    private Long id;
    
    /** 类型：import/exchange/lottery/adjust */
    private String type;
    
    /** 类型描述 */
    private String typeDesc;
    
    /** 积分变动值 */
    private Integer pointsChange;
    
    /** 余额变动值 */
    private BigDecimal balanceChange;
    
    /** 变动后积分余额 */
    private Integer pointsAfter;
    
    /** 变动后余额 */
    private BigDecimal balanceAfter;
    
    /** 变动描述 */
    private String description;
    
    /** 变动时间 */
    private LocalDateTime createdAt;
}
