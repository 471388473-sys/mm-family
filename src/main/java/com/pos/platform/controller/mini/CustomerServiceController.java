package com.pos.platform.controller.mini;

import com.pos.platform.common.constant.ApiPath;
import com.pos.platform.common.result.ApiResponse;
import com.pos.platform.dto.mini.CustomerServiceResponse;
import com.pos.platform.dto.mini.FaqResponse;
import com.pos.platform.service.mini.CustomerServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 小程序端客服控制器
 */
@Tag(name = "客服模块", description = "小程序端客服相关接口")
@RestController
@RequestMapping(ApiPath.Mini.CS)
@RequiredArgsConstructor
public class CustomerServiceController {

    private final CustomerServiceService customerServiceService;

    @Operation(summary = "获取客服信息")
    @GetMapping("/info")
    public ApiResponse<CustomerServiceResponse> getCustomerServiceInfo() {
        CustomerServiceResponse info = customerServiceService.getCustomerServiceInfo();
        return ApiResponse.success(info);
    }

    @Operation(summary = "获取常见问题列表")
    @GetMapping("/faq")
    public ApiResponse<List<FaqResponse>> getFaqList() {
        List<FaqResponse> faqs = customerServiceService.getFaqList();
        return ApiResponse.success(faqs);
    }
}
