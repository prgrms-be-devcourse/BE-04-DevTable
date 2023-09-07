package com.mdh.devtable.waiting.application;

import com.mdh.devtable.waiting.application.dto.ShopTotalWaitingResponse;
import com.mdh.devtable.waiting.infra.persistence.WaitingLine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopWaitingService {

    private final WaitingLine waitingLine;

    public ShopTotalWaitingResponse findShopTotalWaiting(Long shopId) {
        var totalWaiting = waitingLine.findTotalWaiting(shopId);
        return new ShopTotalWaitingResponse(totalWaiting);
    }
}
