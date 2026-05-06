package com.pos.platform.service.impl.mini;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pos.platform.common.error.ErrorCode;
import com.pos.platform.common.exception.BusinessException;
import com.pos.platform.dto.mini.WithdrawalConfigResponse;
import com.pos.platform.dto.mini.WithdrawalRecordResponse;
import com.pos.platform.dto.mini.WithdrawalRequest;
import com.pos.platform.entity.points.PointsAccount;
import com.pos.platform.entity.withdrawal.WithdrawalRecord;
import com.pos.platform.mapper.PointsAccountMapper;
import com.pos.platform.mapper.SystemConfigMapper;
import com.pos.platform.mapper.WithdrawalRecordMapper;
import com.pos.platform.service.mini.WithdrawalService;
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
 * 提现服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawalServiceImpl implements WithdrawalService {

    private final WithdrawalRecordMapper withdrawalRecordMapper;
    private final PointsAccountMapper pointsAccountMapper;
    private final SystemConfigMapper systemConfigMapper;

    /** 配置键常量 */
    private static final String WITHDRAW_MIN_AMOUNT_KEY = "withdraw_min_amount";
    private static final String WITHDRAW_MAX_AMOUNT_KEY = "withdraw_max_amount";
    private static final String WITHDRAW_FIXED_FEE_KEY = "withdraw_fixed_fee";
    private static final String WITHDRAW_FEE_RATE_KEY = "withdraw_fee_rate";
    private static final String WITHDRAW_FEE_ENABLED_KEY = "withdraw_fee_enabled";

    /** 状态描述映射 */
    private static final Map<Integer, String> STATUS_TEXT_MAP = new HashMap<>();
    static {
        STATUS_TEXT_MAP.put(0, "待审核");
        STATUS_TEXT_MAP.put(1, "审核通过");
        STATUS_TEXT_MAP.put(2, "审核拒绝");
        STATUS_TEXT_MAP.put(3, "已打款");
    }

    @Override
    public WithdrawalConfigResponse getConfig() {
        String minAmount = getConfigValue(WITHDRAW_MIN_AMOUNT_KEY, "1");
        String maxAmount = getConfigValue(WITHDRAW_MAX_AMOUNT_KEY, "5000");
        String fixedFee = getConfigValue(WITHDRAW_FIXED_FEE_KEY, "0");
        String feeRate = getConfigValue(WITHDRAW_FEE_RATE_KEY, "0");
        String feeEnabled = getConfigValue(WITHDRAW_FEE_ENABLED_KEY, "false");

        return WithdrawalConfigResponse.builder()
                .minAmount(new BigDecimal(minAmount))
                .maxAmount(new BigDecimal(maxAmount))
                .fixedFee(new BigDecimal(fixedFee))
                .feeRate(new BigDecimal(feeRate))
                .feeEnabled(Boolean.parseBoolean(feeEnabled))
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WithdrawalRecordResponse applyWithdrawal(Long userId, WithdrawalRequest request) {
        BigDecimal amount = request.getAmount();

        // 获取提现配置
        WithdrawalConfigResponse config = getConfig();

        // 校验最低提现金额
        if (amount.compareTo(config.getMinAmount()) < 0) {
            throw new BusinessException(ErrorCode.BALANCE_BELOW_MIN_WITHDRAW,
                    "提现金额不能低于" + config.getMinAmount() + "元");
        }

        // 校验最高提现金额
        if (amount.compareTo(config.getMaxAmount()) > 0) {
            throw new BusinessException(ErrorCode.WITHDRAW_AMOUNT_EXCEED,
                    "提现金额不能超过" + config.getMaxAmount() + "元");
        }

        // 获取用户账户
        PointsAccount account = getAccount(userId);
        if (account == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户账户不存在");
        }

        // 校验余额
        if (account.getBalance().compareTo(amount) < 0) {
            throw new BusinessException(ErrorCode.WITHDRAW_AMOUNT_EXCEED, "可提现余额不足");
        }

        // 检查是否有待审核的提现申请
        LambdaQueryWrapper<WithdrawalRecord> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(WithdrawalRecord::getUserId, userId)
                .eq(WithdrawalRecord::getStatus, 0);
        if (withdrawalRecordMapper.selectCount(pendingWrapper) > 0) {
            throw new BusinessException(ErrorCode.WITHDRAW_PENDING_EXISTS);
        }

        // 计算手续费
        BigDecimal totalFee;
        if (config.getFeeEnabled()) {
            BigDecimal rateFee = amount.multiply(config.getFeeRate())
                    .setScale(2, RoundingMode.DOWN);
            totalFee = config.getFixedFee().add(rateFee);
        } else {
            totalFee = BigDecimal.ZERO;
        }

        // 计算实际到账金额
        BigDecimal actualAmount = amount.subtract(totalFee);

        // 创建提现记录
        WithdrawalRecord record = new WithdrawalRecord();
        record.setUserId(userId);
        record.setAmount(amount);
        record.setFixedFee(config.getFixedFee());
        record.setFeeRate(config.getFeeRate());
        record.setRateFee(config.getFeeEnabled() ?
                amount.multiply(config.getFeeRate()).setScale(2, RoundingMode.DOWN) : BigDecimal.ZERO);
        record.setTotalFee(totalFee);
        record.setActualAmount(actualAmount);
        record.setStatus(0);
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());

        withdrawalRecordMapper.insert(record);

        log.info("用户申请提现成功: userId={}, amount={}, fee={}, actualAmount={}",
                userId, amount, totalFee, actualAmount);

        return convertToResponse(record);
    }

    @Override
    public List<WithdrawalRecordResponse> getRecordList(Long userId, Integer page, Integer size) {
        page = page == null || page < 1 ? 1 : page;
        size = size == null || size < 1 ? 10 : size;

        Page<WithdrawalRecord> recordPage = new Page<>(page, size);
        LambdaQueryWrapper<WithdrawalRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WithdrawalRecord::getUserId, userId)
                .orderByDesc(WithdrawalRecord::getCreatedAt);

        Page<WithdrawalRecord> result = withdrawalRecordMapper.selectPage(recordPage, queryWrapper);

        return result.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Long getRecordCount(Long userId) {
        LambdaQueryWrapper<WithdrawalRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WithdrawalRecord::getUserId, userId);
        return withdrawalRecordMapper.selectCount(queryWrapper);
    }

    @Override
    public BigDecimal getWithdrawableBalance(Long userId) {
        PointsAccount account = getAccount(userId);
        return account != null ? account.getBalance() : BigDecimal.ZERO;
    }

    /**
     * 获取用户账户
     */
    private PointsAccount getAccount(Long userId) {
        return pointsAccountMapper.selectOne(
                new LambdaQueryWrapper<PointsAccount>()
                        .eq(PointsAccount::getUserId, userId)
        );
    }

    /**
     * 获取配置值
     */
    private String getConfigValue(String key, String defaultValue) {
        com.pos.platform.entity.system.SystemConfig config =
                systemConfigMapper.selectOne(new LambdaQueryWrapper<com.pos.platform.entity.system.SystemConfig>()
                        .eq(com.pos.platform.entity.system.SystemConfig::getConfigKey, key));
        return config != null ? config.getConfigValue() : defaultValue;
    }

    /**
     * 转换为响应DTO
     */
    private WithdrawalRecordResponse convertToResponse(WithdrawalRecord record) {
        if (record == null) {
            return null;
        }
        return WithdrawalRecordResponse.builder()
                .id(record.getId())
                .amount(record.getAmount())
                .totalFee(record.getTotalFee())
                .actualAmount(record.getActualAmount())
                .status(record.getStatus())
                .statusText(STATUS_TEXT_MAP.getOrDefault(record.getStatus(), "未知"))
                .rejectReason(record.getRejectReason())
                .createdAt(record.getCreatedAt())
                .approvedAt(record.getApprovedAt())
                .paidAt(record.getPaidAt())
                .build();
    }
}
