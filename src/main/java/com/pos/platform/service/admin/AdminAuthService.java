package com.pos.platform.service.admin;

import com.pos.platform.dto.admin.AdminLoginRequest;
import com.pos.platform.dto.admin.AdminLoginResponse;

/**
 * 管理端认证服务接口
 */
public interface AdminAuthService {

    /**
     * 管理员登录
     * @param request 登录请求
     * @param ip IP地址
     * @return 登录响应
     */
    AdminLoginResponse login(AdminLoginRequest request, String ip);

    /**
     * 获取当前管理员信息
     * @param adminId 管理员ID
     * @return 登录响应
     */
    AdminLoginResponse getAdminInfo(Long adminId);

    /**
     * 退出登录
     * @param adminId 管理员ID
     */
    void logout(Long adminId);
}
