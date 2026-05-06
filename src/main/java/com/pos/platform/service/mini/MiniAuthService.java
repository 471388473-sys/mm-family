package com.pos.platform.service.mini;

import com.pos.platform.dto.mini.*;

/**
 * 小程序认证服务接口
 */
public interface MiniAuthService {

    /**
     * 微信登录
     * @param request 登录请求
     * @param ip IP地址
     * @return 登录响应
     */
    LoginResponse wxLogin(WxLoginRequest request, String ip);

    /**
     * 绑定手机号
     * @param request 绑定请求
     * @param userId 用户ID
     * @return 用户信息
     */
    UserInfoResponse bindPhone(BindPhoneRequest request, Long userId);

    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    UserInfoResponse getUserInfo(Long userId);

    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param nickname 昵称
     * @param avatar 头像
     * @return 用户信息
     */
    UserInfoResponse updateUserInfo(Long userId, String nickname, String avatar);
}
