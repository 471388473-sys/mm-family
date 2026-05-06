package com.pos.platform.service.impl.mini;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pos.platform.dto.mini.CustomerServiceResponse;
import com.pos.platform.dto.mini.FaqResponse;
import com.pos.platform.entity.faq.Faq;
import com.pos.platform.entity.system.SystemConfig;
import com.pos.platform.mapper.FaqMapper;
import com.pos.platform.mapper.SystemConfigMapper;
import com.pos.platform.service.mini.CustomerServiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 客服服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceServiceImpl implements CustomerServiceService {

    private final FaqMapper faqMapper;
    private final SystemConfigMapper systemConfigMapper;

    /** 客服配置键 */
    private static final String CS_HOTLINE_KEY = "cs_hotline";
    private static final String CS_WECHAT_KEY = "cs_wechat";
    private static final String CS_QRCODE_KEY = "cs_qrcode";
    private static final String CS_WORK_HOURS_KEY = "cs_work_hours";

    @Override
    public CustomerServiceResponse getCustomerServiceInfo() {
        String hotline = getConfigValue(CS_HOTLINE_KEY, "");
        String wechat = getConfigValue(CS_WECHAT_KEY, "");
        String qrcodeUrl = getConfigValue(CS_QRCODE_KEY, "");
        String workHours = getConfigValue(CS_WORK_HOURS_KEY, "周一至周五 9:00-18:00");

        // 获取FAQ列表
        List<Faq> faqs = faqMapper.selectList(
                new LambdaQueryWrapper<Faq>()
                        .eq(Faq::getEnabled, 1)
                        .orderByAsc(Faq::getSort)
                        .last("LIMIT 10")
        );

        List<FaqResponse> faqResponses = faqs.stream()
                .map(this::convertToFaqResponse)
                .collect(Collectors.toList());

        return CustomerServiceResponse.builder()
                .hotline(hotline)
                .wechat(wechat)
                .qrcodeUrl(qrcodeUrl)
                .workHours(workHours)
                .faqs(faqResponses)
                .build();
    }

    @Override
    public List<FaqResponse> getFaqList() {
        List<Faq> faqs = faqMapper.selectList(
                new LambdaQueryWrapper<Faq>()
                        .eq(Faq::getEnabled, 1)
                        .orderByAsc(Faq::getSort)
        );

        return faqs.stream()
                .map(this::convertToFaqResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取配置值
     */
    private String getConfigValue(String key, String defaultValue) {
        SystemConfig config = systemConfigMapper.selectOne(
                new LambdaQueryWrapper<SystemConfig>()
                        .eq(SystemConfig::getConfigKey, key)
        );
        return config != null && config.getConfigValue() != null
                ? config.getConfigValue() : defaultValue;
    }

    /**
     * 转换为FAQ响应DTO
     */
    private FaqResponse convertToFaqResponse(Faq faq) {
        if (faq == null) {
            return null;
        }
        return FaqResponse.builder()
                .id(faq.getId())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .sort(faq.getSort())
                .build();
    }
}
