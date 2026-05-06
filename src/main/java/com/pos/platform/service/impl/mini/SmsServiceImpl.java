package com.pos.platform.service.impl.mini;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pos.platform.common.error.ErrorCode;
import com.pos.platform.common.exception.BusinessException;
import com.pos.platform.dto.mini.SendCodeRequest;
import com.pos.platform.dto.mini.SendCodeResponse;
import com.pos.platform.entity.system.SmsLog;
import com.pos.platform.mapper.SmsLogMapper;
import com.pos.platform.service.mini.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * 短信服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    private final SmsLogMapper smsLogMapper;
    
    /** 验证码有效期：5分钟 */
    private static final int VALIDITY_MINUTES = 5;
    
    /** 同一手机号同一场景发送间隔：60秒 */
    private static final int SEND_INTERVAL_SECONDS = 60;
    
    /** 每日同一手机号同一场景最大发送次数 */
    private static final int DAILY_MAX_COUNT = 10;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SendCodeResponse sendCode(SendCodeRequest request, String ip) {
        String phone = request.getPhone();
        String scene = request.getScene();
        
        // 1. 验证手机号格式
        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            throw new BusinessException(ErrorCode.PHONE_FORMAT_ERROR);
        }
        
        // 2. 检查发送频率
        LambdaQueryWrapper<SmsLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SmsLog::getPhone, phone)
                .eq(SmsLog::getScene, scene)
                .gt(SmsLog::getCreatedAt, LocalDateTime.now().minusSeconds(SEND_INTERVAL_SECONDS))
                .orderByDesc(SmsLog::getCreatedAt)
                .last("LIMIT 1");
        
        SmsLog lastSms = smsLogMapper.selectOne(queryWrapper);
        if (lastSms != null) {
            throw new BusinessException(ErrorCode.VERIFY_CODE_FREQUENT);
        }
        
        // 3. 检查每日发送次数
        LambdaQueryWrapper<SmsLog> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(SmsLog::getPhone, phone)
                .eq(SmsLog::getScene, scene)
                .ge(SmsLog::getCreatedAt, LocalDateTime.now().toLocalDate().atStartOfDay());
        long todayCount = smsLogMapper.selectCount(countWrapper);
        
        if (todayCount >= DAILY_MAX_COUNT) {
            throw new BusinessException(ErrorCode.VERIFY_CODE_FREQUENT, "今日发送次数已用完");
        }
        
        // 4. 生成验证码
        String code = generateCode();
        
        // 5. 保存短信记录
        SmsLog smsLog = new SmsLog();
        smsLog.setPhone(phone);
        smsLog.setCode(code);
        smsLog.setScene(scene);
        smsLog.setIp(ip);
        smsLog.setStatus(0); // 未使用
        smsLog.setExpireAt(LocalDateTime.now().plusMinutes(VALIDITY_MINUTES));
        smsLogMapper.insert(smsLog);
        
        // 6. 实际发送短信（这里模拟，实际需要调用短信网关）
        // sendSms(phone, code, scene);
        
        log.info("验证码发送成功: phone={}, scene={}, code={}", phone, scene, code);
        
        return SendCodeResponse.builder()
                .success(true)
                .message("验证码发送成功")
                .remainCount(DAILY_MAX_COUNT - (int) todayCount - 1)
                .build();
    }

    @Override
    public boolean verifyCode(String phone, String code, String scene) {
        LambdaQueryWrapper<SmsLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SmsLog::getPhone, phone)
                .eq(SmsLog::getCode, code)
                .eq(SmsLog::getScene, scene)
                .eq(SmsLog::getStatus, 0) // 未使用
                .gt(SmsLog::getExpireAt, LocalDateTime.now())
                .orderByDesc(SmsLog::getCreatedAt)
                .last("LIMIT 1");
        
        SmsLog smsLog = smsLogMapper.selectOne(queryWrapper);
        return smsLog != null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean verifyAndConsumeCode(String phone, String code, String scene) {
        // 先验证
        boolean valid = verifyCode(phone, code, scene);
        if (!valid) {
            return false;
        }
        
        // 标记为已使用
        LambdaQueryWrapper<SmsLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SmsLog::getPhone, phone)
                .eq(SmsLog::getCode, code)
                .eq(SmsLog::getScene, scene)
                .eq(SmsLog::getStatus, 0)
                .gt(SmsLog::getExpireAt, LocalDateTime.now())
                .orderByDesc(SmsLog::getCreatedAt)
                .last("LIMIT 1");
        
        SmsLog smsLog = smsLogMapper.selectOne(queryWrapper);
        if (smsLog != null) {
            smsLog.setStatus(1); // 已使用
            smsLogMapper.updateById(smsLog);
            return true;
        }
        return false;
    }
    
    /**
     * 生成6位数字验证码
     */
    private String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
