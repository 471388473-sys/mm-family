package com.pos.platform.dto.admin;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理端机具响应DTO
 */
@Data
public class MachineResponse {
    
    /** 机具ID */
    private Long id;
    
    /** 机具编码（SN码） */
    private String machineCode;
    
    /** 产品ID */
    private Long productId;
    
    /** 产品名称 */
    private String productName;
    
    /** 绑定客户ID */
    private Long customerId;
    
    /** 客户名称 */
    private String customerName;
    
    /** 绑定手机号 */
    private String bindPhone;
    
    /** 状态：0-未绑定 1-已绑定 */
    private Integer status;
    
    /** 状态描述 */
    private String statusDesc;
    
    /** 绑定时间 */
    private LocalDateTime bindAt;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
}
