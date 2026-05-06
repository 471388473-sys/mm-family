package com.pos.platform.service.impl.mini;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pos.platform.common.error.ErrorCode;
import com.pos.platform.common.exception.BusinessException;
import com.pos.platform.dto.mini.LotteryConfigResponse;
import com.pos.platform.dto.mini.LotteryRecordResponse;
import com.pos.platform.dto.mini.LotteryResultResponse;
import com.pos.platform.entity.lottery.LotteryConfig;
import com.pos.platform.entity.lottery.LotteryDaily;
import com.pos.platform.entity.lottery.LotteryPrize;
import com.pos.platform.entity.lottery.LotteryRecord;
import com.pos.platform.entity.points.PointsAccount;
import com.pos.platform.entity.points.PointsRecord;
import com.pos.platform.mapper.LotteryConfigMapper;
import com.pos.platform.mapper.LotteryDailyMapper;
import com.pos.platform.mapper.LotteryPrizeMapper;
import com.pos.platform.mapper.LotteryRecordMapper;
import com.pos.platform.mapper.PointsAccountMapper;
import com.pos.platform.mapper.PointsRecordMapper;
import com.pos.platform.service.mini.LotteryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 抽奖服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LotteryServiceImpl implements LotteryService {

    private final LotteryConfigMapper lotteryConfigMapper;
    private final LotteryPrizeMapper lotteryPrizeMapper;
    private final LotteryDailyMapper lotteryDailyMapper;
    private final LotteryRecordMapper lotteryRecordMapper;
    private final PointsAccountMapper pointsAccountMapper;
    private final PointsRecordMapper pointsRecordMapper;

    @Override
    public LotteryConfigResponse getConfig(Long userId) {
        // 获取抽奖配置
        LotteryConfig config = lotteryConfigMapper.selectOne(new LambdaQueryWrapper<>());
        if (config == null || config.getEnabled() != 1) {
            return LotteryConfigResponse.builder()
                    .enabled(false)
                    .title("抽奖活动")
                    .remainingChances(0)
                    .prizes(new ArrayList<>())
                    .build();
        }

        // 获取奖项列表
        List<LotteryPrize> prizes = lotteryPrizeMapper.selectList(
                new LambdaQueryWrapper<LotteryPrize>()
                        .eq(LotteryPrize::getEnabled, 1)
                        .orderByAsc(LotteryPrize::getSort)
        );

        List<LotteryConfigResponse.PrizeResponse> prizeResponses = prizes.stream()
                .map(p -> LotteryConfigResponse.PrizeResponse.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .points(p.getPoints())
                        .probability(p.getProbability())
                        .build())
                .collect(Collectors.toList());

        // 获取用户今日剩余抽奖次数
        int remainingChances = getRemainingChances(userId, config);

        return LotteryConfigResponse.builder()
                .enabled(true)
                .title(config.getTitle())
                .threshold(config.getThreshold())
                .dailyLimit(config.getDailyLimit())
                .remainingChances(remainingChances)
                .prizes(prizeResponses)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LotteryResultResponse draw(Long userId) {
        // 获取抽奖配置
        LotteryConfig config = lotteryConfigMapper.selectOne(new LambdaQueryWrapper<>());
        if (config == null || config.getEnabled() != 1) {
            throw new BusinessException(ErrorCode.LOTTERY_NOT_ENABLED);
        }

        // 检查积分是否满足阈值
        PointsAccount account = pointsAccountMapper.selectOne(
                new LambdaQueryWrapper<PointsAccount>()
                        .eq(PointsAccount::getUserId, userId)
        );
        if (account == null || account.getPoints() < config.getThreshold()) {
            throw new BusinessException(ErrorCode.LOTTERY_CHANCES_INSUFFICIENT,
                    "积分不足，当前需要" + config.getThreshold() + "积分才能抽奖");
        }

        // 检查每日抽奖次数
        int remainingChances = getRemainingChances(userId, config);
        if (remainingChances <= 0) {
            throw new BusinessException(ErrorCode.LOTTERY_DAILY_LIMIT);
        }

        // 获取可用奖项
        List<LotteryPrize> prizes = lotteryPrizeMapper.selectList(
                new LambdaQueryWrapper<LotteryPrize>()
                        .eq(LotteryPrize::getEnabled, 1)
        );

        if (prizes.isEmpty()) {
            throw new BusinessException(ErrorCode.LOTTERY_PRIZE_EMPTY);
        }

        // 扣减积分
        int pointsBefore = account.getPoints();
        account.setPoints(pointsBefore - config.getThreshold());
        pointsAccountMapper.updateById(account);

        // 执行抽奖
        LotteryPrize wonPrize = executeLottery(prizes);

        // 更新抽奖次数
        updateDailyDrawCount(userId, config);

        // 如果中奖，发放积分奖励
        int pointsAfter = pointsBefore - config.getThreshold();
        if (wonPrize != null) {
            // 检查奖品是否还有库存
            if (wonPrize.getTotalCount() != -1 && wonPrize.getRemainingCount() <= 0) {
                // 奖品已发完，视为未中奖
                wonPrize = null;
            } else {
                // 发放积分奖励
                account.setPoints(account.getPoints() + wonPrize.getPoints());
                account.setTotalLotteryWinnings(
                        account.getTotalLotteryWinnings() + wonPrize.getPoints());
                pointsAccountMapper.updateById(account);

                // 扣减奖品库存
                if (wonPrize.getTotalCount() != -1) {
                    wonPrize.setRemainingCount(wonPrize.getRemainingCount() - 1);
                    lotteryPrizeMapper.updateById(wonPrize);
                }

                pointsAfter = account.getPoints();

                // 记录积分变动
                recordPointsChange(userId, wonPrize.getPoints(), pointsAfter);
            }
        }

        // 记录抽奖记录
        LotteryRecord record = new LotteryRecord();
        record.setUserId(userId);
        record.setPointsBefore(pointsBefore);
        record.setPointsAfter(pointsAfter);
        if (wonPrize != null) {
            record.setPrizeId(wonPrize.getId());
            record.setPrizeName(wonPrize.getName());
            record.setPrizePoints(wonPrize.getPoints());
        }
        record.setCreatedAt(LocalDateTime.now());
        lotteryRecordMapper.insert(record);

        log.info("用户抽奖: userId={}, 是否中奖={}, 奖项={}, 积分变化={}->{}",
                userId, wonPrize != null,
                wonPrize != null ? wonPrize.getName() : "未中奖",
                pointsBefore, pointsAfter);

        return LotteryResultResponse.builder()
                .won(wonPrize != null)
                .prizeId(wonPrize != null ? wonPrize.getId() : null)
                .prizeName(wonPrize != null ? wonPrize.getName() : null)
                .prizePoints(wonPrize != null ? wonPrize.getPoints() : null)
                .currentPoints(account.getPoints())
                .remainingChances(getRemainingChances(userId, config))
                .pointsBefore(pointsBefore)
                .pointsAfter(pointsAfter)
                .build();
    }

    @Override
    public List<LotteryRecordResponse> getRecordList(Long userId, Integer page, Integer size) {
        page = page == null || page < 1 ? 1 : page;
        size = size == null || size < 1 ? 10 : size;

        Page<LotteryRecord> recordPage = new Page<>(page, size);
        LambdaQueryWrapper<LotteryRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LotteryRecord::getUserId, userId)
                .orderByDesc(LotteryRecord::getCreatedAt);

        Page<LotteryRecord> result = lotteryRecordMapper.selectPage(recordPage, queryWrapper);

        return result.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Long getRecordCount(Long userId) {
        LambdaQueryWrapper<LotteryRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LotteryRecord::getUserId, userId);
        return lotteryRecordMapper.selectCount(queryWrapper);
    }

    /**
     * 获取剩余抽奖次数
     */
    private int getRemainingChances(Long userId, LotteryConfig config) {
        LotteryDaily daily = lotteryDailyMapper.selectOne(
                new LambdaQueryWrapper<LotteryDaily>()
                        .eq(LotteryDaily::getUserId, userId)
                        .eq(LotteryDaily::getDate, LocalDate.now())
        );

        int usedCount = daily != null ? daily.getDrawCount() : 0;
        return Math.max(0, config.getDailyLimit() - usedCount);
    }

    /**
     * 更新每日抽奖次数
     */
    private void updateDailyDrawCount(Long userId, LotteryConfig config) {
        LotteryDaily daily = lotteryDailyMapper.selectOne(
                new LambdaQueryWrapper<LotteryDaily>()
                        .eq(LotteryDaily::getUserId, userId)
                        .eq(LotteryDaily::getDate, LocalDate.now())
        );

        if (daily == null) {
            daily = new LotteryDaily();
            daily.setUserId(userId);
            daily.setDate(LocalDate.now());
            daily.setDrawCount(1);
            daily.setCreatedAt(LocalDateTime.now());
            daily.setUpdatedAt(LocalDateTime.now());
            lotteryDailyMapper.insert(daily);
        } else {
            daily.setDrawCount(daily.getDrawCount() + 1);
            daily.setUpdatedAt(LocalDateTime.now());
            lotteryDailyMapper.updateById(daily);
        }
    }

    /**
     * 执行抽奖算法
     */
    private LotteryPrize executeLottery(List<LotteryPrize> prizes) {
        // 计算总概率
        BigDecimal totalProbability = prizes.stream()
                .filter(p -> p.getRemainingCount() != 0) // 过滤掉已发完的奖品
                .map(LotteryPrize::getProbability)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalProbability.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        // 生成随机数
        double random = Math.random();
        BigDecimal cumulative = BigDecimal.ZERO;

        for (LotteryPrize prize : prizes) {
            // 跳过已发完的奖品
            if (prize.getTotalCount() != -1 && prize.getRemainingCount() <= 0) {
                continue;
            }

            // 计算中奖概率
            BigDecimal probability = prize.getProbability().divide(totalProbability, 4, BigDecimal.ROUND_HALF_UP);
            cumulative = cumulative.add(probability);

            if (random <= cumulative.doubleValue()) {
                return prize;
            }
        }

        return null; // 未中奖
    }

    /**
     * 记录积分变动
     */
    private void recordPointsChange(Long userId, Integer prizePoints, Integer pointsAfter) {
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setType("lottery");
        record.setPointsChange(prizePoints);
        record.setBalanceChange(null);
        record.setPointsAfter(pointsAfter);
        record.setBalanceAfter(null);
        record.setDescription(String.format("抽奖获得：+%d积分", prizePoints));
        record.setCreatedAt(LocalDateTime.now());
        pointsRecordMapper.insert(record);
    }

    /**
     * 转换为响应DTO
     */
    private LotteryRecordResponse convertToResponse(LotteryRecord record) {
        if (record == null) {
            return null;
        }
        return LotteryRecordResponse.builder()
                .id(record.getId())
                .prizeName(record.getPrizeName() != null ? record.getPrizeName() : "未中奖")
                .prizePoints(record.getPrizePoints() != null ? record.getPrizePoints() : 0)
                .createdAt(record.getCreatedAt())
                .build();
    }
}
