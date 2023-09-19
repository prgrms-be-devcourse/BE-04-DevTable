package com.mdh.user.shop.application;

import com.mdh.common.shop.persistence.ShopRepository;
import com.mdh.common.waiting.persistence.WaitingLine;
import com.mdh.user.shop.application.dto.ShopDetailInfoResponse;
import com.mdh.user.shop.application.dto.ShopResponse;
import com.mdh.user.shop.application.dto.ShopResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class ShopService {

    private final ShopRepository shopRepository;
    private final WaitingLine waitingLine;

    public ShopResponses findByConditionWithWaiting(MultiValueMap<String, String> cond, Pageable pageable) {
        var shopQueryResponse = shopRepository.searchShopCondition(cond, pageable);
        var shopResponses = shopQueryResponse.stream()
                .map(queryDto -> {
                    var totalWaiting = waitingLine.findTotalWaiting(queryDto.shopId());
                    return new ShopResponse(queryDto, totalWaiting);
                })
                .toList();

        var totalCount = shopRepository.searchShopConditionCount(cond);
        var pageResponse = PageableExecutionUtils.getPage(shopResponses, pageable, totalCount::fetchOne);
        return new ShopResponses(pageResponse);
    }

    @Transactional(readOnly = true)
    public ShopDetailInfoResponse findShopDetailsById(Long shopId) {
        return shopRepository.findById(shopId)
                .map(ShopDetailInfoResponse::from)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 매장입니다: " + shopId));
    }

}