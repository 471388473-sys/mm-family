package com.pos.platform.service.admin;

import com.pos.platform.common.result.PageResponse;
import com.pos.platform.dto.admin.CustomerListRequest;
import com.pos.platform.dto.admin.CustomerResponse;

/**
 * 客户管理服务接口
 */
public interface CustomerService {

    /**
     * 查询客户列表
     * @param request 查询请求
     * @return 分页响应
     */
    PageResponse<CustomerResponse> listCustomers(CustomerListRequest request);

    /**
     * 获取客户详情
     * @param userId 用户ID
     * @return 客户信息
     */
    CustomerResponse getCustomerDetail(Long userId);

    /**
     * 启用/禁用客户
     * @param userId 用户ID
     * @param status 状态
     */
    void updateCustomerStatus(Long userId, Integer status);
}
