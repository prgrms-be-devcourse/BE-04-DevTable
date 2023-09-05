package com.mdh.devtable.waiting.application;

import com.mdh.devtable.waiting.Waiting;
import com.mdh.devtable.waiting.WaitingLine;
import com.mdh.devtable.waiting.WaitingPeople;
import com.mdh.devtable.waiting.infra.persistence.ShopWaitingRepository;
import com.mdh.devtable.waiting.infra.persistence.WaitingRepository;
import com.mdh.devtable.waiting.presentation.dto.WaitingCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final ShopWaitingRepository shopWaitingRepository;
    private final WaitingLine waitingLine;

    @Transactional
    public Long createWaiting(WaitingCreateRequest waitingCreateRequest) {
        var shopId = waitingCreateRequest.shopId();
        var shopWaiting = shopWaitingRepository.findById(shopId)
            .orElseThrow(() -> new IllegalStateException("해당 매장에 웨이팅 정보가 존재하지 않습니다. shopId : " + shopId));

        var adultCount = waitingCreateRequest.adultCount();
        var childCount = waitingCreateRequest.childCount();
        var waitingPeople = new WaitingPeople(adultCount, childCount);

        var userId = waitingCreateRequest.userId();
        var waiting = Waiting.builder()
            .shopWaiting(shopWaiting)
            .waitingPeople(waitingPeople)
            .userId(userId)
            .build();

        var savedWaiting = waitingRepository.save(waiting);
        var waitingId = savedWaiting.getId();
        var createdDate = savedWaiting.getCreatedDate();
        waitingLine.save(shopId, waitingId, createdDate);

        return waitingId;
    }
}