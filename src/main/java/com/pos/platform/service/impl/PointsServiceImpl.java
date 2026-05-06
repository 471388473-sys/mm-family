package com.pos.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pos.platform.common.error.ErrorCode;
import com.pos.platform.common.exception.BusinessException;
import com.pos.platform.dto.response.PointsAccountResponse;
import com.pos.platform.dto.response.PointsRecordResponse;
import com.pos.platform.entity.points.PointsAccount;
import com.pos.platform.entity.points.PointsRecord;
import com.pos.platform.entity.system.SystemConfig;
import com.pos.platform.mapper.PointsAccountMapper;
import com.pos.platform.mapper.PointsRecordMapper;
import com.pos.platform.mapper.SystemConfigMapper;
import com.pos.platform.service.PointsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 积分服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PointsServiceImpl implements PointsService {

    private final PointsAccountMapper pointsAccountMapper;
    private final PointsRecordMapper pointsRecordMapper;
    private final SystemConfigMapper systemConfigMapper;

    /** 积分兑换比例配置键 */
    private static final String POINTS_EXCHANGE_RATE_KEY = "points_exchange_rate";

    /** 积分类型描述映射 */
    private static final Map<String, String> TYPE_DESC_MAP = new HashMap<>();
    
    static {
        TYPE_DESC_MAP.put("import", "积分导入");
        TYPE_DESC_MAP.put("exchange", "积分兑换");
        TYPE_DESC_MAP.put("lottery", "抽奖获得");
        TYPE_DESC_MAP.put("adjust", "积分调整");
    }

    @Override
    public PointsAccountResponse getAccountInfo(Long userId) {
        PointsAccount account = getOrCreateAccount(userId);
        return convertToAccountResponse(account);
    }

    @Override
    public BigDecimal getExchangeRate() {
        SystemConfig config = systemConfigMapper.selectOne(
            new LambdaQueryWrapper<SystemConfig>()
                .eq(SystemConfig::getConfigKey, POINTS_EXCHANGE_RATE_KEY)
        );
        
        if (config == null || config.getConfigValue() == null) {
            log.warn("积分兑换比例未配置");
            throw new BusinessException(ErrorCode.EXCHANGE_RATE_NOT_CONFIG);
        }
        
        try {
            return new BigDecimal(config.getConfigValue());
        } catch (NumberFormatException e) {
            log.error("积分兑换比例配置格式错误: {}", config.getConfigValue());
            throw new BusinessException(ErrorCode.EXCHANGE_RATE_NOT_CONFIG);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PointsAccountResponse exchange(Long userId, Integer points) {
        // 参数校验
        if (points == null || points <= 0) {
            throw new BusinessException(ErrorCode.EXCHANGE_AMOUNT_INVALID);
        }
        
        // 获取兑换比例
        BigDecimal exchangeRate = getExchangeRate();
        
        // 获取或创建账户
        PointsAccount account = getOrCreateAccount(userId);
        
        // 校验积分是否足够
        if (account.getPoints() < points) {
            throw new BusinessException(ErrorCode.POINTS_INSUFFICIENT);
        }
        
        // 计算兑换金额
        BigDecimal exchangeAmount = new BigDecimal(points)
            .multiply(exchangeRate)
            .setScale(2, RoundingMode.DOWN);
        
        // 使用乐观锁更新账户
        LambdaUpdateWrapper<PointsAccount> updateWrapper = new LambdaUpdateWrapper<PointsAccount>()
            .eq(PointsAccount::getId, account.getId())
            .eq(PointsAccount::getVersion, account.getVersion())
            .set(PointsAccount::getPoints, account.getPoints() - points)
            .set(PointsAccount::getBalance, account.getBalance().add(exchangeAmount))
            .set(PointsAccount::getTotalExchanged, account.getTotalExchanged().add(exchangeAmount))
            .set(PointsAccount::getVersion, account.getVersion() + 1);
        
        int updated = pointsAccountMapper.update(null, updateWrapper);
        if (updated == 0) {
            throw new BusinessException(ErrorCode.SERVER_ERROR, "兑换失败，请重试");
        }
        
        // 记录积分变动
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setType("exchange");
        record.setPointsChange(-points);
        record.setBalanceChange(exchangeAmount);
        record.setPointsAfter(account.getPoints() - points);
        record.setBalanceAfter(account.getBalance().add(exchangeAmount));
        record.setDescription(String.format("积分兑换：消耗%d积分，兑换%.2f元", points, exchangeAmount));
        record.setCreatedAt(LocalDateTime.now());
        pointsRecordMapper.insert(record);
        
        // 重新查询更新后的账户
        PointsAccount updatedAccount = pointsAccountMapper.selectById(account.getId());
        return convertToAccountResponse(updatedAccount);
    }

    @Override
    public List<PointsRecordResponse> getRecordList(Long userId, String type, Integer page, Integer size) {
        page = page == null || page < 1 ? 1 : page;
        size = size == null || size < 1 ? 10 : size;
        
        Page<PointsRecord> recordPage = new Page<>(page, size);
        LambdaQueryWrapper<PointsRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PointsRecord::getUserId, userId);
        if (type != null && !type.isEmpty()) {
            queryWrapper.eq(PointsRecord::getType, type);
        }
        queryWrapper.orderByDesc(PointsRecord::getCreatedAt);
        
        Page<PointsRecord> result = pointsRecordMapper.selectPage(recordPage, queryWrapper);
        
        return result.getRecords().stream()
            .map(this::convertToRecordResponse)
            .collect(Collectors.toList());
    }

    @Override
    public Long getRecordCount(Long userId, String type) {
        LambdaQueryWrapper<PointsRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PointsRecord::getUserId, userId);
        if (type != null && !type.isEmpty()) {
            queryWrapper.eq(PointsRecord::getType, type);
        }
        return pointsRecordMapper.selectCount(queryWrapper);
    }

    /**
     * 获取或创建积分账户
     */
    private PointsAccount getOrCreateAccount(Long userId) {
        PointsAccount account = pointsAccountMapper.selectOne(
            new LambdaQueryWrapper<PointsAccount>()
                .eq(PointsAccount::getUserId, userId)
        );
        
        if (account == null) {
            account = new PointsAccount();
            account.setUserId(userId);
            account.setPoints(0);
            account.setBalance(BigDecimal.ZERO);
            account.setTotalExchanged(BigDecimal.ZERO);
            account.setTotalWithdrawn(BigDecimal.ZERO);
            account.setTotalLotteryWinnings(0);
            account.setVersion(0);
            account.setCreatedAt(LocalDateTime.now());
            account.setUpdatedAt(LocalDateTime.now());
            pointsAccountMapper.insert(account);
        }
        
        return account;
    }

    /**
     * 转换为账户响应DTO
     */
    private PointsAccountResponse convertToAccountResponse(PointsAccount account) {
        if (account == null) {
            return null;
        }
        PointsAccountResponse response = new PointsAccountResponse();
        response.setId(account.getId());
        response.setUserId(account.getUserId());
        response.setPoints(account.getPoints());
        response.setBalance(account.getBalance());
        response.setTotalExchanged(account.getTotalExchanged());
        response.setTotalWithdrawn(account.getTotalWithdrawn());
        response.setTotalLotteryWinnings(account.getTotalLotteryWinnings());
        response.setCreatedAt(account.getCreatedAt());
        return response;
    }

    /**
     * 转换为记录响应DTO
     */
    private PointsRecordResponse convertToRecordResponse(PointsRecord record) {
        if (record == null) {
            return null;
        }
        PointsRecordResponse response = new PointsRecordResponse();
        response.setId(record.getId());
        response.setType(record.getType());
        response.setTypeDesc(TYPE_DESC_MAP.getOrDefault(record.getType(), record.getType()));
        response.setPointsChange(record.getPointsChange());
        response.setBalanceChange(record.getBalanceChange());
        response.setPointsAfter(record.getPointsAfter());
        response.setBalanceAfter(record.getBalanceAfter());
        response.setDescription(record.getDescription());
        response.setCreatedAt(record.getCreatedAt());
        return response;
    }
}
