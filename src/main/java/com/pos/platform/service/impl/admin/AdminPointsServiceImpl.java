package com.pos.platform.service.impl.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pos.platform.common.error.ErrorCode;
import com.pos.platform.common.exception.BusinessException;
import com.pos.platform.dto.admin.*;
import com.pos.platform.entity.points.PointsAccount;
import com.pos.platform.entity.points.PointsRecord;
import com.pos.platform.entity.user.User;
import com.pos.platform.mapper.PointsAccountMapper;
import com.pos.platform.mapper.PointsRecordMapper;
import com.pos.platform.mapper.UserMapper;
import com.pos.platform.service.admin.AdminPointsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理端积分服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminPointsServiceImpl implements AdminPointsService {

    private final PointsAccountMapper pointsAccountMapper;
    private final PointsRecordMapper pointsRecordMapper;
    private final UserMapper userMapper;

    /** 积分类型描述映射 */
    private static final Map<String, String> TYPE_DESC_MAP = new HashMap<>();
    
    static {
        TYPE_DESC_MAP.put("import", "积分导入");
        TYPE_DESC_MAP.put("exchange", "积分兑换");
        TYPE_DESC_MAP.put("lottery", "抽奖获得");
        TYPE_DESC_MAP.put("adjust", "积分调整");
    }

    @Override
    public List<AdminPointsRecordResponse> getPointsRecordList(PointsRecordQueryRequest query) {
        int page = query.getPage() == null || query.getPage() < 1 ? 1 : query.getPage();
        int size = query.getSize() == null || query.getSize() < 1 ? 10 : query.getSize();
        
        Page<PointsRecord> recordPage = new Page<>(page, size);
        LambdaQueryWrapper<PointsRecord> queryWrapper = buildRecordQueryWrapper(query);
        queryWrapper.orderByDesc(PointsRecord::getCreatedAt);
        
        Page<PointsRecord> result = pointsRecordMapper.selectPage(recordPage, queryWrapper);
        
        return result.getRecords().stream()
            .map(this::convertToRecordResponse)
            .collect(Collectors.toList());
    }

    @Override
    public Long getPointsRecordCount(PointsRecordQueryRequest query) {
        LambdaQueryWrapper<PointsRecord> queryWrapper = buildRecordQueryWrapper(query);
        return pointsRecordMapper.selectCount(queryWrapper);
    }

    @Override
    public AdminPointsAccountResponse getUserPointsAccount(Long userId) {
        PointsAccount account = pointsAccountMapper.selectOne(
            new LambdaQueryWrapper<PointsAccount>()
                .eq(PointsAccount::getUserId, userId)
        );
        
        if (account == null) {
            // 返回默认账户信息
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
            }
            
            AdminPointsAccountResponse response = new AdminPointsAccountResponse();
            response.setUserId(userId);
            response.setUserName(user.getName());
            response.setPhone(user.getPhone());
            response.setPoints(0);
            response.setBalance("0.00");
            response.setTotalPoints(0);
            response.setTotalEarnings("0.00");
            return response;
        }
        
        return convertToAccountResponse(account);
    }

    @Override
    public List<AdminPointsAccountResponse> getUserPointsAccountList(String phone, Integer page, Integer size) {
        page = page == null || page < 1 ? 1 : page;
        size = size == null || size < 1 ? 10 : size;
        
        // 如果有手机号查询，先获取匹配的用户
        List<User> users;
        if (StringUtils.hasText(phone)) {
            users = userMapper.selectList(
                new LambdaQueryWrapper<User>()
                    .like(User::getPhone, phone)
            );
            if (users.isEmpty()) {
                return List.of();
            }
            List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
            
            Page<PointsAccount> recordPage = new Page<>(page, size);
            LambdaQueryWrapper<PointsAccount> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(PointsAccount::getUserId, userIds);
            queryWrapper.orderByDesc(PointsAccount::getUpdatedAt);
            
            Page<PointsAccount> result = pointsAccountMapper.selectPage(recordPage, queryWrapper);
            return result.getRecords().stream()
                .map(this::convertToAccountResponse)
                .collect(Collectors.toList());
        } else {
            Page<PointsAccount> recordPage = new Page<>(page, size);
            LambdaQueryWrapper<PointsAccount> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.orderByDesc(PointsAccount::getUpdatedAt);
            
            Page<PointsAccount> result = pointsAccountMapper.selectPage(recordPage, queryWrapper);
            return result.getRecords().stream()
                .map(this::convertToAccountResponse)
                .collect(Collectors.toList());
        }
    }

    @Override
    public Long getUserPointsAccountCount(String phone) {
        if (StringUtils.hasText(phone)) {
            List<User> users = userMapper.selectList(
                new LambdaQueryWrapper<User>()
                    .like(User::getPhone, phone)
            );
            if (users.isEmpty()) {
                return 0L;
            }
            List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
            
            return pointsAccountMapper.selectCount(
                new LambdaQueryWrapper<PointsAccount>()
                    .in(PointsAccount::getUserId, userIds)
            );
        }
        return pointsAccountMapper.selectCount(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjustPoints(PointsAdjustRequest request, Long operatorId) {
        if (request.getUserId() == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "用户ID不能为空");
        }
        
        User user = userMapper.selectById(request.getUserId());
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        
        PointsAccount account = pointsAccountMapper.selectOne(
            new LambdaQueryWrapper<PointsAccount>()
                .eq(PointsAccount::getUserId, request.getUserId())
        );
        
        if (account == null) {
            account = new PointsAccount();
            account.setUserId(request.getUserId());
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
        
        int newPoints = account.getPoints();
        BigDecimal newBalance = account.getBalance();
        
        // 调整积分
        if (request.getPoints() != null && request.getPoints() != 0) {
            newPoints = account.getPoints() + request.getPoints();
            if (newPoints < 0) {
                throw new BusinessException(ErrorCode.POINTS_INSUFFICIENT, "积分不足，无法减少");
            }
        }
        
        // 调整余额
        if (request.getBalance() != null && request.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            newBalance = account.getBalance().add(request.getBalance());
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException(ErrorCode.BALANCE_INSUFFICIENT, "余额不足，无法减少");
            }
        }
        
        // 更新账户
        LambdaUpdateWrapper<PointsAccount> updateWrapper = new LambdaUpdateWrapper<PointsAccount>()
            .eq(PointsAccount::getId, account.getId())
            .eq(PointsAccount::getVersion, account.getVersion());
        
        if (request.getPoints() != null) {
            updateWrapper.set(PointsAccount::getPoints, newPoints);
        }
        if (request.getBalance() != null) {
            updateWrapper.set(PointsAccount::getBalance, newBalance);
        }
        updateWrapper.set(PointsAccount::getVersion, account.getVersion() + 1);
        
        int updated = pointsAccountMapper.update(null, updateWrapper);
        if (updated == 0) {
            throw new BusinessException(ErrorCode.SERVER_ERROR, "调整失败，请重试");
        }
        
        // 记录积分变动
        PointsRecord record = new PointsRecord();
        record.setUserId(request.getUserId());
        record.setType("adjust");
        record.setPointsChange(request.getPoints() != null ? request.getPoints() : 0);
        record.setBalanceChange(request.getBalance() != null ? request.getBalance() : BigDecimal.ZERO);
        record.setPointsAfter(newPoints);
        record.setBalanceAfter(newBalance);
        record.setDescription(request.getReason() != null ? request.getReason() : "管理员调整");
        record.setOperatorId(operatorId);
        record.setCreatedAt(LocalDateTime.now());
        pointsRecordMapper.insert(record);
        
        log.info("管理员调整用户积分成功: userId={}, points={}, balance={}, operatorId={}", 
            request.getUserId(), request.getPoints(), request.getBalance(), operatorId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importPoints(Long userId, Integer points, String description, Long operatorId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "用户ID不能为空");
        }
        if (points == null || points <= 0) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "导入积分必须大于0");
        }
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        
        PointsAccount account = pointsAccountMapper.selectOne(
            new LambdaQueryWrapper<PointsAccount>()
                .eq(PointsAccount::getUserId, userId)
        );
        
        if (account == null) {
            account = new PointsAccount();
            account.setUserId(userId);
            account.setPoints(points);
            account.setBalance(BigDecimal.ZERO);
            account.setTotalExchanged(BigDecimal.ZERO);
            account.setTotalWithdrawn(BigDecimal.ZERO);
            account.setTotalLotteryWinnings(0);
            account.setVersion(0);
            account.setCreatedAt(LocalDateTime.now());
            account.setUpdatedAt(LocalDateTime.now());
            pointsAccountMapper.insert(account);
            
            // 记录变动
            PointsRecord record = new PointsRecord();
            record.setUserId(userId);
            record.setType("import");
            record.setPointsChange(points);
            record.setBalanceChange(BigDecimal.ZERO);
            record.setPointsAfter(points);
            record.setBalanceAfter(BigDecimal.ZERO);
            record.setDescription(description != null ? description : "积分导入");
            record.setOperatorId(operatorId);
            record.setCreatedAt(LocalDateTime.now());
            pointsRecordMapper.insert(record);
        } else {
            // 更新账户
            LambdaUpdateWrapper<PointsAccount> updateWrapper = new LambdaUpdateWrapper<PointsAccount>()
                .eq(PointsAccount::getId, account.getId())
                .eq(PointsAccount::getVersion, account.getVersion())
                .set(PointsAccount::getPoints, account.getPoints() + points)
                .set(PointsAccount::getVersion, account.getVersion() + 1);
            
            int updated = pointsAccountMapper.update(null, updateWrapper);
            if (updated == 0) {
                throw new BusinessException(ErrorCode.SERVER_ERROR, "导入失败，请重试");
            }
            
            // 记录变动
            PointsRecord record = new PointsRecord();
            record.setUserId(userId);
            record.setType("import");
            record.setPointsChange(points);
            record.setBalanceChange(BigDecimal.ZERO);
            record.setPointsAfter(account.getPoints() + points);
            record.setBalanceAfter(account.getBalance());
            record.setDescription(description != null ? description : "积分导入");
            record.setOperatorId(operatorId);
            record.setCreatedAt(LocalDateTime.now());
            pointsRecordMapper.insert(record);
        }
        
        log.info("管理员导入积分成功: userId={}, points={}, operatorId={}", userId, points, operatorId);
    }

    /**
     * 构建积分记录查询条件
     */
    private LambdaQueryWrapper<PointsRecord> buildRecordQueryWrapper(PointsRecordQueryRequest query) {
        LambdaQueryWrapper<PointsRecord> queryWrapper = new LambdaQueryWrapper<>();
        
        // 按手机号查询用户
        if (StringUtils.hasText(query.getPhone())) {
            List<User> users = userMapper.selectList(
                new LambdaQueryWrapper<User>()
                    .like(User::getPhone, query.getPhone())
            );
            if (users.isEmpty()) {
                queryWrapper.eq(PointsRecord::getId, -1L); // 无结果
            } else {
                List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
                queryWrapper.in(PointsRecord::getUserId, userIds);
            }
        }
        
        if (StringUtils.hasText(query.getType())) {
            queryWrapper.eq(PointsRecord::getType, query.getType());
        }
        
        if (StringUtils.hasText(query.getStartTime())) {
            LocalDateTime startTime = LocalDateTime.parse(query.getStartTime() + " 00:00:00");
            queryWrapper.ge(PointsRecord::getCreatedAt, startTime);
        }
        
        if (StringUtils.hasText(query.getEndTime())) {
            LocalDateTime endTime = LocalDateTime.parse(query.getEndTime() + " 23:59:59");
            queryWrapper.le(PointsRecord::getCreatedAt, endTime);
        }
        
        return queryWrapper;
    }

    /**
     * 转换为积分记录响应DTO
     */
    private AdminPointsRecordResponse convertToRecordResponse(PointsRecord record) {
        if (record == null) {
            return null;
        }
        
        AdminPointsRecordResponse response = new AdminPointsRecordResponse();
        response.setId(record.getId());
        response.setUserId(record.getUserId());
        response.setType(record.getType());
        response.setTypeDesc(TYPE_DESC_MAP.getOrDefault(record.getType(), record.getType()));
        response.setPointsChange(record.getPointsChange());
        response.setBalanceChange(record.getBalanceChange() != null ? record.getBalanceChange().toString() : "0.00");
        response.setPointsAfter(record.getPointsAfter());
        response.setBalanceAfter(record.getBalanceAfter() != null ? record.getBalanceAfter().toString() : "0.00");
        response.setDescription(record.getDescription());
        response.setOperatorId(record.getOperatorId());
        response.setCreatedAt(record.getCreatedAt());
        
        // 查询用户信息
        if (record.getUserId() != null) {
            User user = userMapper.selectById(record.getUserId());
            if (user != null) {
                response.setUserName(user.getName());
                response.setPhone(user.getPhone());
            }
        }
        
        // 查询操作人信息
        if (record.getOperatorId() != null) {
            User operator = userMapper.selectById(record.getOperatorId());
            if (operator != null) {
                response.setOperatorName(operator.getName());
            }
        }
        
        return response;
    }

    /**
     * 转换为账户响应DTO
     */
    private AdminPointsAccountResponse convertToAccountResponse(PointsAccount account) {
        if (account == null) {
            return null;
        }
        
        AdminPointsAccountResponse response = new AdminPointsAccountResponse();
        response.setUserId(account.getUserId());
        response.setPoints(account.getPoints());
        response.setBalance(account.getBalance() != null ? account.getBalance().toString() : "0.00");
        response.setTotalPoints(account.getPoints());
        response.setTotalEarnings(account.getTotalExchanged() != null ? 
            account.getTotalExchanged().toString() : "0.00");
        
        // 查询用户信息
        if (account.getUserId() != null) {
            User user = userMapper.selectById(account.getUserId());
            if (user != null) {
                response.setUserName(user.getName());
                response.setPhone(user.getPhone());
            }
        }
        
        return response;
    }
}
