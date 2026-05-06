package com.pos.platform.dto.mini;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 激活步骤响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivationStepResponse {

    /** 步骤ID */
    private Long id;

    /** 产品ID */
    private Long productId;

    /** 步骤标题 */
    private String title;

    /** 步骤说明 */
    private String description;

    /** 引导图片URL */
    private String imageUrl;

    /** 排序值 */
    private Integer sort;
}
