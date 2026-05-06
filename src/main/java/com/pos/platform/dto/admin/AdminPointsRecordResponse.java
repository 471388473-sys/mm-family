package com.pos.platform.dto.admin;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理端积分记录响应DTO
 */
@Data
public class AdminPointsRecordResponse {
    
    /** 记录ID */
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 用户名称 */
    private String userName;
    
    /** 用户手机号 */
    private String phone;
    
    /** 类型：import/exchange/lottery/adjust */
    private String type;
    
    /** 类型描述 */
    private String typeDesc;
    
    /** 积分变动值 */
    private Integer pointsChange;
    
    /** 余额变动值 */
    private String balanceChange;
    
    /** 变动后积分余额 */
    private Integer pointsAfter;
    
    /** 变动后余额 */
    private String balanceAfter;
    
    /** 变动描述 */
    private String description;
    
    /** 操作人ID */
    private Long operatorId;
    
    /** 操作人名称 */
    private String operatorName;
    
    /** 变动时间 */
    private LocalDateTime createdAt;
}
