package com.pos.platform.dto.admin;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 管理端积分调整请求DTO
 */
@Data
public class PointsAdjustRequest {
    
    /** 用户ID */
    private Long userId;
    
    /** 调整积分值（正数增加，负数减少） */
    private Integer points;
    
    /** 调整余额值 */
    private BigDecimal balance;
    
    /** 调整原因 */
    private String reason;
}
