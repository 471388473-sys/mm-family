package com.pos.platform.dto.admin;

import lombok.Data;

/**
 * 管理端机具查询请求DTO
 */
@Data
public class MachineQueryRequest {
    
    /** 机具编码（SN码） */
    private String machineCode;
    
    /** 绑定手机号 */
    private String bindPhone;
    
    /** 状态：0-未绑定 1-已绑定 */
    private Integer status;
    
    /** 页码，默认1 */
    private Integer page = 1;
    
    /** 每页条数，默认10 */
    private Integer size = 10;
}
