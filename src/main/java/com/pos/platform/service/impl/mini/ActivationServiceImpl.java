package com.pos.platform.service.impl.mini;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pos.platform.common.error.ErrorCode;
import com.pos.platform.common.exception.BusinessException;
import com.pos.platform.dto.mini.ActivationMaterialResponse;
import com.pos.platform.dto.mini.ActivationProductResponse;
import com.pos.platform.dto.mini.ActivationStepResponse;
import com.pos.platform.entity.activation.ActivationMaterial;
import com.pos.platform.entity.activation.ActivationProduct;
import com.pos.platform.entity.activation.ActivationStep;
import com.pos.platform.mapper.ActivationMaterialMapper;
import com.pos.platform.mapper.ActivationProductMapper;
import com.pos.platform.mapper.ActivationStepMapper;
import com.pos.platform.service.mini.ActivationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 激活流程服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActivationServiceImpl implements ActivationService {

    private final ActivationProductMapper productMapper;
    private final ActivationMaterialMapper materialMapper;
    private final ActivationStepMapper stepMapper;

    @Override
    public List<ActivationProductResponse> getProductList() {
        LambdaQueryWrapper<ActivationProduct> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(ActivationProduct::getSort);
        
        List<ActivationProduct> products = productMapper.selectList(queryWrapper);
        
        return products.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ActivationProductResponse getProductDetail(Long productId) {
        ActivationProduct product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return buildProductDetail(product);
    }

    private ActivationProductResponse convertToProductResponse(ActivationProduct product) {
        return ActivationProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .sort(product.getSort())
                .build();
    }

    private ActivationProductResponse buildProductDetail(ActivationProduct product) {
        // 查询材料列表
        LambdaQueryWrapper<ActivationMaterial> materialQuery = new LambdaQueryWrapper<>();
        materialQuery.eq(ActivationMaterial::getProductId, product.getId())
                     .orderByAsc(ActivationMaterial::getSort);
        List<ActivationMaterial> materials = materialMapper.selectList(materialQuery);
        
        // 查询步骤列表
        LambdaQueryWrapper<ActivationStep> stepQuery = new LambdaQueryWrapper<>();
        stepQuery.eq(ActivationStep::getProductId, product.getId())
                 .orderByAsc(ActivationStep::getSort);
        List<ActivationStep> steps = stepMapper.selectList(stepQuery);
        
        return ActivationProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .sort(product.getSort())
                .materials(convertToMaterialResponses(materials))
                .steps(convertToStepResponses(steps))
                .build();
    }

    private List<ActivationMaterialResponse> convertToMaterialResponses(List<ActivationMaterial> materials) {
        if (materials == null || materials.isEmpty()) {
            return Collections.emptyList();
        }
        return materials.stream()
                .map(m -> ActivationMaterialResponse.builder()
                        .id(m.getId())
                        .productId(m.getProductId())
                        .name(m.getName())
                        .required(m.getRequired())
                        .requiredDesc(m.getRequired() != null && m.getRequired() == 1 ? "必填" : "选填")
                        .sort(m.getSort())
                        .build())
                .collect(Collectors.toList());
    }

    private List<ActivationStepResponse> convertToStepResponses(List<ActivationStep> steps) {
        if (steps == null || steps.isEmpty()) {
            return Collections.emptyList();
        }
        return steps.stream()
                .map(s -> ActivationStepResponse.builder()
                        .id(s.getId())
                        .productId(s.getProductId())
                        .title(s.getTitle())
                        .description(s.getDescription())
                        .imageUrl(s.getImageUrl())
                        .sort(s.getSort())
                        .build())
                .collect(Collectors.toList());
    }
}
