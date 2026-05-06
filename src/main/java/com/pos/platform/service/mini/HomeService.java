package com.pos.platform.service.mini;

import com.pos.platform.dto.mini.HomeDataResponse;

/**
 * 首页服务接口
 */
public interface HomeService {

    /**
     * 获取首页数据
     * @param userId 用户ID
     * @return 首页数据
     */
    HomeDataResponse getHomeData(Long userId);
}
