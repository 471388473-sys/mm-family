package com.pos.platform.dto.mini;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 激活产品响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivationProductResponse {

    /** 产品ID */
    private Long id;

    /** 产品名称 */
    private String name;

    /** 产品描述 */
    private String description;

    /** 排序值 */
    private Integer sort;

    /** 材料列表 */
    private List<ActivationMaterialResponse> materials;

    /** 步骤列表 */
    private List<ActivationStepResponse> steps;
}
