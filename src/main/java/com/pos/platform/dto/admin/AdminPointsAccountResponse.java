package com.pos.platform.dto.admin;

import lombok.Data;

/**
 * 管理端用户积分信息响应DTO
 */
@Data
public class AdminPointsAccountResponse {
    
    /** 用户ID */
    private Long userId;
    
    /** 用户名称 */
    private String userName;
    
    /** 手机号 */
    private String phone;
    
    /** 当前积分 */
    private Integer points;
    
    /** 当前余额 */
    private String balance;
    
    /** 累计积分 */
    private Integer totalPoints;
    
    /** 累计收益 */
    private String totalEarnings;
}
