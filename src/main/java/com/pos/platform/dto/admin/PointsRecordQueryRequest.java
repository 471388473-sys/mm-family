package com.pos.platform.dto.admin;

import lombok.Data;

/**
 * 管理端积分记录查询请求DTO
 */
@Data
public class PointsRecordQueryRequest {
    
    /** 用户手机号 */
    private String phone;
    
    /** 记录类型：import/exchange/lottery/adjust */
    private String type;
    
    /** 开始时间 */
    private String startTime;
    
    /** 结束时间 */
    private String endTime;
    
    /** 页码，默认1 */
    private Integer page = 1;
    
    /** 每页条数，默认10 */
    private Integer size = 10;
}
