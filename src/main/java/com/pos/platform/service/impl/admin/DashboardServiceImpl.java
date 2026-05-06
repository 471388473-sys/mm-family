package com.pos.platform.service.impl.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pos.platform.dto.admin.DashboardResponse;
import com.pos.platform.entity.machine.Machine;
import com.pos.platform.entity.machine.MachineTransaction;
import com.pos.platform.entity.points.PointsAccount;
import com.pos.platform.entity.points.PointsRecord;
import com.pos.platform.entity.user.User;
import com.pos.platform.entity.withdrawal.WithdrawalRecord;
import com.pos.platform.mapper.*;
import com.pos.platform.service.admin.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据概览服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserMapper userMapper;
    private final MachineMapper machineMapper;
    private final MachineTransactionMapper transactionMapper;
    private final WithdrawalRecordMapper withdrawalRecordMapper;
    private final PointsRecordMapper pointsRecordMapper;
    private final PointsAccountMapper pointsAccountMapper;

    @Override
    public DashboardResponse getDashboard() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        DashboardResponse response = DashboardResponse.builder()
                .todayNewUsers(getNewUserCount(today))
                .yesterdayNewUsers(getNewUserCount(yesterday))
                .totalUsers(getTotalUserCount())
                .todayTransactionAmount(getTransactionAmount(today))
                .yesterdayTransactionAmount(getTransactionAmount(yesterday))
                .totalTransactionAmount(getTotalTransactionAmount())
                .todayTransactionCount(getTransactionCount(today))
                .yesterdayTransactionCount(getTransactionCount(yesterday))
                .totalTransactionCount(getTotalTransactionCount())
                .todayWithdrawals(getWithdrawalCount(today))
                .pendingWithdrawals(getPendingWithdrawalCount())
                .todayLotteryCount(getTodayLotteryCount())
                .boundMachines(getBoundMachineCount())
                .totalMachines(getTotalMachineCount())
                .todayPoints(getTodayPoints(today))
                .totalPoints(getTotalPoints())
                .build();

        return response;
    }

    @Override
    public DashboardResponse getTodayData() {
        LocalDate today = LocalDate.now();
        
        return DashboardResponse.builder()
                .todayNewUsers(getNewUserCount(today))
                .todayTransactionAmount(getTransactionAmount(today))
                .todayTransactionCount(getTransactionCount(today))
                .todayWithdrawals(getWithdrawalCount(today))
                .todayLotteryCount(getTodayLotteryCount())
                .todayPoints(getTodayPoints(today))
                .build();
    }

    // 统计方法

    private Long getNewUserCount(LocalDate date) {
        return userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .ge(User::getCreatedAt, date.atStartOfDay())
                        .lt(User::getCreatedAt, date.plusDays(1).atStartOfDay())
        );
    }

    private Long getTotalUserCount() {
        return userMapper.selectCount(null);
    }

    private String getTransactionAmount(LocalDate date) {
        BigDecimal amount = transactionMapper.selectList(
                new LambdaQueryWrapper<MachineTransaction>()
                        .ge(MachineTransaction::getTransactionTime, date.atStartOfDay())
                        .lt(MachineTransaction::getTransactionTime, date.plusDays(1).atStartOfDay())
        ).stream().map(MachineTransaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return amount.toString();
    }

    private String getTotalTransactionAmount() {
        BigDecimal amount = transactionMapper.selectList(null)
                .stream().map(MachineTransaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return amount.toString();
    }

    private Long getTransactionCount(LocalDate date) {
        return transactionMapper.selectCount(
                new LambdaQueryWrapper<MachineTransaction>()
                        .ge(MachineTransaction::getTransactionTime, date.atStartOfDay())
                        .lt(MachineTransaction::getTransactionTime, date.plusDays(1).atStartOfDay())
        );
    }

    private Long getTotalTransactionCount() {
        return transactionMapper.selectCount(null);
    }

    private Long getWithdrawalCount(LocalDate date) {
        return withdrawalRecordMapper.selectCount(
                new LambdaQueryWrapper<WithdrawalRecord>()
                        .ge(WithdrawalRecord::getCreatedAt, date.atStartOfDay())
                        .lt(WithdrawalRecord::getCreatedAt, date.plusDays(1).atStartOfDay())
        );
    }

    private Long getPendingWithdrawalCount() {
        return withdrawalRecordMapper.selectCount(
                new LambdaQueryWrapper<WithdrawalRecord>()
                        .eq(WithdrawalRecord::getStatus, 0) // 待审核
        );
    }

    private Long getTodayLotteryCount() {
        // 暂时返回0，后续有抽奖记录表后可完善
        return 0L;
    }

    private Long getBoundMachineCount() {
        return machineMapper.selectCount(
                new LambdaQueryWrapper<Machine>()
                        .isNotNull(Machine::getUserId)
                        .ne(Machine::getUserId, 0L)
        );
    }

    private Long getTotalMachineCount() {
        return machineMapper.selectCount(null);
    }

    private Long getTodayPoints(LocalDate date) {
        Long points = pointsRecordMapper.selectList(
                new LambdaQueryWrapper<PointsRecord>()
                        .ge(PointsRecord::getCreatedAt, date.atStartOfDay())
                        .lt(PointsRecord::getCreatedAt, date.plusDays(1).atStartOfDay())
        ).stream().map(PointsRecord::getPoints).reduce(0L, Long::sum);
        return points;
    }

    private Long getTotalPoints() {
        return pointsAccountMapper.selectList(null)
                .stream().map(PointsAccount::getBalance).reduce(0L, Long::sum);
    }
}
