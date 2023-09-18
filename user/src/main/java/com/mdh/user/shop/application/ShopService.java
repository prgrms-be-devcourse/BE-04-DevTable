package com.mdh.user.shop.application;

import com.mdh.common.shop.persistence.ShopRepository;
import com.mdh.common.waiting.persistence.WaitingLine;
import com.mdh.user.shop.application.dto.ShopResponse;
import com.mdh.user.shop.application.dto.ShopResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

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
}
