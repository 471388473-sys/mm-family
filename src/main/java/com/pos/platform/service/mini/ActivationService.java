package com.pos.platform.service.mini;

import com.pos.platform.dto.mini.ActivationProductResponse;

import java.util.List;

/**
 * 激活流程服务接口
 */
public interface ActivationService {

    /**
     * 获取激活产品列表
     * @return 激活产品列表
     */
    List<ActivationProductResponse> getProductList();

    /**
     * 获取激活产品详情
     * @param productId 产品ID
     * @return 激活产品详情
     */
    ActivationProductResponse getProductDetail(Long productId);
}
