package com.pos.platform.service.impl.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pos.platform.common.error.ErrorCode;
import com.pos.platform.common.exception.BusinessException;
import com.pos.platform.common.result.PageResponse;
import com.pos.platform.dto.admin.CustomerListRequest;
import com.pos.platform.dto.admin.CustomerResponse;
import com.pos.platform.entity.points.PointsAccount;
import com.pos.platform.entity.user.User;
import com.pos.platform.entity.withdrawal.WithdrawalRecord;
import com.pos.platform.mapper.MachineMapper;
import com.pos.platform.mapper.PointsAccountMapper;
import com.pos.platform.mapper.UserMapper;
import com.pos.platform.mapper.WithdrawalRecordMapper;
import com.pos.platform.service.admin.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 客户管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final UserMapper userMapper;
    private final PointsAccountMapper pointsAccountMapper;
    private final WithdrawalRecordMapper withdrawalRecordMapper;
    private final MachineMapper machineMapper;

    @Override
    public PageResponse<CustomerResponse> listCustomers(CustomerListRequest request) {
        // 构建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.and(w -> w
                    .like(User::getPhone, request.getKeyword())
                    .or()
                    .like(User::getNickname, request.getKeyword())
                    .or()
                    .like(User::getOpenid, request.getKeyword()));
        }
        
        if (request.getStatus() != null) {
            wrapper.eq(User::getStatus, request.getStatus());
        }
        
        if (StringUtils.hasText(request.getStartDate())) {
            wrapper.ge(User::getCreatedAt, LocalDate.parse(request.getStartDate()).atStartOfDay());
        }
        
        if (StringUtils.hasText(request.getEndDate())) {
            wrapper.le(User::getCreatedAt, LocalDate.parse(request.getEndDate()).atTime(LocalTime.MAX));
        }
        
        wrapper.orderByDesc(User::getCreatedAt);

        // 分页查询
        int page = request.getPage() != null ? request.getPage() : 1;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 20;
        
        Page<User> pageResult = userMapper.selectPage(new Page<>(page, pageSize), wrapper);
        List<User> users = pageResult.getRecords();

        if (users.isEmpty()) {
            return PageResponse.of(List.of(), page, pageSize, 0);
        }

        // 获取用户ID列表
        List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());

        // 批量查询积分账户
        Map<Long, PointsAccount> pointsAccountMap = pointsAccountMapper.selectList(
                new LambdaQueryWrapper<PointsAccount>().in(PointsAccount::getUserId, userIds)
        ).stream().collect(Collectors.toMap(PointsAccount::getUserId, p -> p));

        // 批量查询可提现余额
        Map<Long, BigDecimal> withdrawableMap = withdrawalRecordMapper.selectList(
                new LambdaQueryWrapper<WithdrawalRecord>()
                        .in(WithdrawalRecord::getUserId, userIds)
                        .eq(WithdrawalRecord::getStatus, 2) // 已完成
        ).stream().collect(Collectors.groupingBy(
                WithdrawalRecord::getUserId,
                Collectors.reducing(BigDecimal.ZERO, WithdrawalRecord::getAmount, BigDecimal::add)
        ));

        // 批量查询机具数量
        Map<Long, Long> machineCountMap = machineMapper.selectList(
                new LambdaQueryWrapper<com.pos.platform.entity.machine.Machine>()
                        .in(com.pos.platform.entity.machine.Machine::getUserId, userIds)
                        .isNotNull(com.pos.platform.entity.machine.Machine::getUserId)
        ).stream().collect(Collectors.groupingBy(
                com.pos.platform.entity.machine.Machine::getUserId,
                Collectors.counting()
        ));

        // 转换为响应对象
        List<CustomerResponse> customerList = users.stream().map(user -> {
            CustomerResponse response = new CustomerResponse();
            BeanUtils.copyProperties(user, response);
            
            // 设置状态名称
            response.setStatusName(user.getStatus() == 1 ? "正常" : "禁用");
            
            // 设置积分余额
            PointsAccount account = pointsAccountMap.get(user.getId());
            response.setPointsBalance(account != null ? account.getBalance() : 0L);
            
            // 设置可提现余额
            response.setWithdrawableBalance(withdrawableMap.getOrDefault(user.getId(), BigDecimal.ZERO));
            
            // 设置机具数量
            response.setMachineCount(machineCountMap.getOrDefault(user.getId(), 0L).intValue());
            
            return response;
        }).collect(Collectors.toList());

        return PageResponse.of(customerList, page, pageSize, pageResult.getTotal());
    }

    @Override
    public CustomerResponse getCustomerDetail(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户不存在");
        }

        CustomerResponse response = new CustomerResponse();
        BeanUtils.copyProperties(user, response);
        response.setStatusName(user.getStatus() == 1 ? "正常" : "禁用");

        // 查询积分余额
        PointsAccount account = pointsAccountMapper.selectOne(
                new LambdaQueryWrapper<PointsAccount>().eq(PointsAccount::getUserId, userId)
        );
        response.setPointsBalance(account != null ? account.getBalance() : 0L);

        // 查询可提现余额
        BigDecimal withdrawable = withdrawalRecordMapper.selectList(
                new LambdaQueryWrapper<WithdrawalRecord>()
                        .eq(WithdrawalRecord::getUserId, userId)
                        .eq(WithdrawalRecord::getStatus, 2)
        ).stream().map(WithdrawalRecord::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setWithdrawableBalance(withdrawable);

        // 查询机具数量
        Long machineCount = machineMapper.selectCount(
                new LambdaQueryWrapper<com.pos.platform.entity.machine.Machine>()
                        .eq(com.pos.platform.entity.machine.Machine::getUserId, userId)
        );
        response.setMachineCount(machineCount != null ? machineCount.intValue() : 0);

        return response;
    }

    @Override
    public void updateCustomerStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户不存在");
        }

        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("更新用户状态: userId={}, status={}", userId, status);
    }
}
