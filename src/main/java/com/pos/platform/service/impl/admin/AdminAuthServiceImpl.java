package com.pos.platform.service.impl.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pos.platform.common.error.ErrorCode;
import com.pos.platform.common.exception.BusinessException;
import com.pos.platform.config.JwtProperties;
import com.pos.platform.dto.admin.AdminLoginRequest;
import com.pos.platform.dto.admin.AdminLoginResponse;
import com.pos.platform.entity.admin.AdminUser;
import com.pos.platform.entity.system.OperationLog;
import com.pos.platform.mapper.AdminUserMapper;
import com.pos.platform.mapper.OperationLogMapper;
import com.pos.platform.service.admin.AdminAuthService;
import com.pos.platform.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理端认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {

    private final AdminUserMapper adminUserMapper;
    private final OperationLogMapper operationLogMapper;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;

    /** 角色名称映射 */
    private static final Map<String, String> ROLE_NAME_MAP = new HashMap<>();

    static {
        ROLE_NAME_MAP.put("admin", "超级管理员");
        ROLE_NAME_MAP.put("service", "客服");
    }

    @Override
    public AdminLoginResponse login(AdminLoginRequest request, String ip) {
        // 查询管理员
        LambdaQueryWrapper<AdminUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminUser::getUsername, request.getUsername());
        AdminUser admin = adminUserMapper.selectOne(wrapper);

        if (admin == null) {
            log.warn("管理员登录失败，用户不存在: {}", request.getUsername());
            throw new BusinessException(ErrorCode.ADMIN_LOGIN_FAILED);
        }

        // 验证密码 (MD5简单验证，生产环境建议使用BCrypt)
        String inputPassword = md5(request.getPassword());
        if (!inputPassword.equals(admin.getPassword())) {
            log.warn("管理员登录失败，密码错误: {}", request.getUsername());
            throw new BusinessException(ErrorCode.ADMIN_LOGIN_FAILED);
        }

        // 检查账号状态
        if (admin.getStatus() == 0) {
            log.warn("管理员登录失败，账号已禁用: {}", request.getUsername());
            throw new BusinessException(ErrorCode.ADMIN_DISABLED);
        }

        // 生成Token
        String token = jwtUtil.generateAdminToken(admin.getId(), admin.getUsername(), admin.getRole());

        // 更新最后登录时间
        admin.setLastLoginAt(LocalDateTime.now());
        adminUserMapper.updateById(admin);

        // 记录操作日志
        saveOperationLog(admin.getId(), "登录系统", ip);

        log.info("管理员登录成功: {}", request.getUsername());

        return AdminLoginResponse.builder()
                .token(token)
                .adminId(admin.getId())
                .username(admin.getUsername())
                .realName(admin.getRealName())
                .role(admin.getRole())
                .roleName(ROLE_NAME_MAP.getOrDefault(admin.getRole(), admin.getRole()))
                .expireIn(jwtProperties.getAdminExpireTime() / 1000)
                .build();
    }

    @Override
    public AdminLoginResponse getAdminInfo(Long adminId) {
        AdminUser admin = adminUserMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        return AdminLoginResponse.builder()
                .adminId(admin.getId())
                .username(admin.getUsername())
                .realName(admin.getRealName())
                .role(admin.getRole())
                .roleName(ROLE_NAME_MAP.getOrDefault(admin.getRole(), admin.getRole()))
                .expireIn(jwtProperties.getAdminExpireTime() / 1000)
                .build();
    }

    @Override
    public void logout(Long adminId) {
        // 可以在这里添加Token黑名单等逻辑
        log.info("管理员退出登录: {}", adminId);
    }

    /**
     * MD5加密
     */
    private String md5(String text) {
        return DigestUtils.md5DigestAsHex(text.getBytes());
    }

    /**
     * 保存操作日志
     */
    private void saveOperationLog(Long adminId, String content, String ip) {
        OperationLog log = new OperationLog();
        log.setAdminId(adminId);
        log.setContent(content);
        log.setIp(ip);
        log.setCreatedAt(LocalDateTime.now());
        operationLogMapper.insert(log);
    }
}
