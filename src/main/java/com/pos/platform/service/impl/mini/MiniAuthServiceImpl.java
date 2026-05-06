package com.pos.platform.service.impl.mini;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pos.platform.common.error.ErrorCode;
import com.pos.platform.common.exception.BusinessException;
import com.pos.platform.dto.mini.*;
import com.pos.platform.entity.system.SmsLog;
import com.pos.platform.entity.user.User;
import com.pos.platform.mapper.SmsLogMapper;
import com.pos.platform.mapper.UserMapper;
import com.pos.platform.service.mini.MiniAuthService;
import com.pos.platform.service.mini.SmsService;
import com.pos.platform.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 小程序认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MiniAuthServiceImpl implements MiniAuthService {

    private final UserMapper userMapper;
    private final SmsLogMapper smsLogMapper;
    private final JwtUtil jwtUtil;
    private final SmsService smsService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse wxLogin(WxLoginRequest request, String ip) {
        // 1. 微信登录逻辑（这里模拟，实际需要调用微信API获取openid）
        // 实际项目中应该通过code调用微信接口获取openid
        String openid = request.getCode(); // 模拟使用code作为openid
        
        // 2. 查询用户是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getOpenid, openid);
        User user = userMapper.selectOne(queryWrapper);
        
        boolean isNewUser = false;
        if (user == null) {
            // 新用户，创建用户
            user = new User();
            user.setOpenid(openid);
            user.setNickname(request.getNickname());
            user.setAvatar(request.getAvatar());
            user.setStatus(1);
            userMapper.insert(user);
            isNewUser = true;
            log.info("新用户注册成功: openid={}", openid);
        } else {
            // 老用户，更新登录信息
            if (request.getNickname() != null) {
                user.setNickname(request.getNickname());
            }
            if (request.getAvatar() != null) {
                user.setAvatar(request.getAvatar());
            }
            user.setLastLoginAt(LocalDateTime.now());
            userMapper.updateById(user);
        }
        
        // 3. 生成Token
        String token = jwtUtil.generateMiniToken(user.getId(), openid);
        
        // 4. 构建响应
        return LoginResponse.builder()
                .userId(user.getId())
                .token(token)
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .isNewUser(isNewUser)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoResponse bindPhone(BindPhoneRequest request, Long userId) {
        // 1. 验证手机号格式
        String phone = request.getPhone();
        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            throw new BusinessException(ErrorCode.PHONE_FORMAT_ERROR);
        }
        
        // 2. 验证验证码
        boolean valid = smsService.verifyAndConsumeCode(phone, request.getCode(), "login");
        if (!valid) {
            throw new BusinessException(ErrorCode.VERIFY_CODE_ERROR);
        }
        
        // 3. 更新用户手机号
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        
        // 检查手机号是否已被其他用户绑定
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        queryWrapper.ne(User::getId, userId);
        Long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.USER_EXISTS);
        }
        
        user.setPhone(phone);
        userMapper.updateById(user);
        
        log.info("用户绑定手机号成功: userId={}, phone={}", userId, phone);
        
        return buildUserInfoResponse(user);
    }

    @Override
    public UserInfoResponse getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return buildUserInfoResponse(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoResponse updateUserInfo(Long userId, String nickname, String avatar) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        
        if (nickname != null) {
            user.setNickname(nickname);
        }
        if (avatar != null) {
            user.setAvatar(avatar);
        }
        userMapper.updateById(user);
        
        log.info("用户信息更新成功: userId={}", userId);
        return buildUserInfoResponse(user);
    }
    
    private UserInfoResponse buildUserInfoResponse(User user) {
        return UserInfoResponse.builder()
                .userId(user.getId())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .status(user.getStatus())
                .lastLoginAt(user.getLastLoginAt() != null ? user.getLastLoginAt().toString() : null)
                .build();
    }
}
