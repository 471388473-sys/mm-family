package com.pos.platform.service.mini;

import com.pos.platform.dto.mini.CustomerServiceResponse;
import com.pos.platform.dto.mini.FaqResponse;

import java.util.List;

/**
 * 客服服务接口
 */
public interface CustomerServiceService {

    /**
     * 获取客服信息
     */
    CustomerServiceResponse getCustomerServiceInfo();

    /**
     * 获取常见问题列表
     */
    List<FaqResponse> getFaqList();
}
