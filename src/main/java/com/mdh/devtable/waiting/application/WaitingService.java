package com.mdh.devtable.waiting.application;

import com.mdh.devtable.waiting.domain.Waiting;
import com.mdh.devtable.waiting.domain.WaitingPeople;
import com.mdh.devtable.waiting.infra.persistence.ShopWaitingRepository;
import com.mdh.devtable.waiting.infra.persistence.WaitingLine;
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

        WaitingPeople waitingPeople = createWaitingPeople(waitingCreateRequest);

        var userId = waitingCreateRequest.userId();
        var waiting = Waiting.builder()
            .shopWaiting(shopWaiting)
            .waitingPeople(waitingPeople)
            .userId(userId)
            .build();

        var savedWaiting = waitingRepository.save(waiting); // 저장
        saveWaitingLine(shopId, savedWaiting);

        return savedWaiting.getId();
    }

    private void saveWaitingLine(Long shopId, Waiting savedWaiting) {
        var waitingId = savedWaiting.getId();
        var createdDate = savedWaiting.getCreatedDate();
        waitingLine.save(shopId, waitingId, createdDate); // 웨이팅 라인 저장
    }

    private WaitingPeople createWaitingPeople(WaitingCreateRequest waitingCreateRequest) {
        var adultCount = waitingCreateRequest.adultCount();
        var childCount = waitingCreateRequest.childCount();
        return new WaitingPeople(adultCount, childCount);
    }
}