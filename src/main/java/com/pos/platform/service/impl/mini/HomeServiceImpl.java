package com.pos.platform.service.impl.mini;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pos.platform.common.error.ErrorCode;
import com.pos.platform.common.exception.BusinessException;
import com.pos.platform.dto.mini.AnnouncementResponse;
import com.pos.platform.dto.mini.HomeDataResponse;
import com.pos.platform.entity.pos.PosApplication;
import com.pos.platform.entity.points.PointsAccount;
import com.pos.platform.entity.user.User;
import com.pos.platform.mapper.PosApplicationMapper;
import com.pos.platform.mapper.PointsAccountMapper;
import com.pos.platform.mapper.UserMapper;
import com.pos.platform.service.mini.AnnouncementService;
import com.pos.platform.service.mini.HomeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 首页服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final UserMapper userMapper;
    private final PointsAccountMapper pointsAccountMapper;
    private final PosApplicationMapper posApplicationMapper;
    private final AnnouncementService announcementService;

    @Override
    public HomeDataResponse getHomeData(Long userId) {
        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        // 查询积分账户
        LambdaQueryWrapper<PointsAccount> accountQuery = new LambdaQueryWrapper<>();
        accountQuery.eq(PointsAccount::getUserId, userId);
        PointsAccount account = pointsAccountMapper.selectOne(accountQuery);

        // 查询POS申请状态
        LambdaQueryWrapper<PosApplication> posQuery = new LambdaQueryWrapper<>();
        posQuery.eq(PosApplication::getUserId, userId)
                .orderByDesc(PosApplication::getCreatedAt)
                .last("LIMIT 1");
        PosApplication posApplication = posApplicationMapper.selectOne(posQuery);

        // 获取最新公告
        List<AnnouncementResponse> announcements = announcementService.getLatestAnnouncements(5);

        // 构建响应
        return HomeDataResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .points(account != null ? account.getPoints() : 0)
                .balance(account != null ? account.getBalance() : null)
                .totalExchanged(account != null ? account.getTotalExchanged() : null)
                .totalWithdrawn(account != null ? account.getTotalWithdrawn() : null)
                .announcements(announcements)
                .hasAppliedPos(posApplication != null)
                .posApplyStatus(posApplication != null ? posApplication.getStatus() : null)
                .lotteryChances(0)  // 抽奖次数后续实现
                .build();
    }
}
