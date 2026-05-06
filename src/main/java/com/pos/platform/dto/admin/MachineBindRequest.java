package com.pos.platform.dto.admin;

import lombok.Data;

/**
 * 管理端机具绑定请求DTO
 */
@Data
public class MachineBindRequest {
    
    /** 机具ID */
    private Long machineId;
    
    /** 机具编码 */
    private String machineCode;
    
    /** 客户ID */
    private Long customerId;
    
    /** 客户手机号 */
    private String phone;
}
