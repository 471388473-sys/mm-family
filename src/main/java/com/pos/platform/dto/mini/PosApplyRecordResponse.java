package com.pos.platform.dto.mini;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * POS申请记录响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PosApplyRecordResponse {

    /** 申请ID */
    private Long id;

    /** 申请人姓名 */
    private String name;

    /** 申请人手机号 */
    private String phone;

    /** 收货地址 */
    private String address;

    /** 状态：1-已提交 2-已处理 */
    private Integer status;

    /** 状态描述 */
    private String statusDesc;

    /** 处理时间 */
    private LocalDateTime processedAt;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
