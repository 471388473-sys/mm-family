package com.pos.platform.controller.admin;

import com.pos.platform.common.constant.ApiPath;
import com.pos.platform.common.result.ApiResponse;
import com.pos.platform.common.result.PageResponse;
import com.pos.platform.dto.admin.CustomerListRequest;
import com.pos.platform.dto.admin.CustomerResponse;
import com.pos.platform.service.admin.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 客户管理控制器
 */
@RestController
@RequestMapping(ApiPath.Admin.CUSTOMER)
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /**
     * 查询客户列表
     */
    @GetMapping
    public ApiResponse<PageResponse<CustomerResponse>> listCustomers(CustomerListRequest request) {
        PageResponse<CustomerResponse> response = customerService.listCustomers(request);
        return ApiResponse.success(response);
    }

    /**
     * 获取客户详情
     */
    @GetMapping("/{userId}")
    public ApiResponse<CustomerResponse> getCustomerDetail(@PathVariable Long userId) {
        CustomerResponse response = customerService.getCustomerDetail(userId);
        return ApiResponse.success(response);
    }

    /**
     * 启用/禁用客户
     */
    @PutMapping("/{userId}/status")
    public ApiResponse<Void> updateCustomerStatus(@PathVariable Long userId,
                                                  @RequestParam Integer status) {
        customerService.updateCustomerStatus(userId, status);
        return ApiResponse.success();
    }
}
