package com.pos.platform.controller.mini;

import com.pos.platform.common.constant.ApiPath;
import com.pos.platform.common.result.ApiResponse;
import com.pos.platform.dto.mini.ActivationProductResponse;
import com.pos.platform.service.mini.ActivationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序激活流程控制器
 */
@RestController
@RequestMapping(ApiPath.Mini.ACTIVATION)
@RequiredArgsConstructor
public class ActivationController {

    private final ActivationService activationService;

    /**
     * 获取激活产品列表
     */
    @GetMapping("/products")
    public ApiResponse<List<ActivationProductResponse>> getProductList() {
        List<ActivationProductResponse> list = activationService.getProductList();
        return ApiResponse.success(list);
    }

    /**
     * 获取激活产品详情（包含材料清单和激活步骤）
     * @param productId 产品ID
     */
    @GetMapping("/products/{productId}")
    public ApiResponse<ActivationProductResponse> getProductDetail(@PathVariable Long productId) {
        ActivationProductResponse response = activationService.getProductDetail(productId);
        return ApiResponse.success(response);
    }
}
