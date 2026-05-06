package com.pos.platform.dto.mini;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 激活材料响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivationMaterialResponse {

    /** 材料ID */
    private Long id;

    /** 产品ID */
    private Long productId;

    /** 材料名称 */
    private String name;

    /** 是否必填：0-选填 1-必填 */
    private Integer required;

    /** 是否必填描述 */
    private String requiredDesc;

    /** 排序值 */
    private Integer sort;
}
