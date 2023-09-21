package com.mdh.user.waiting.application;

import com.mdh.common.waiting.domain.event.WaitingCanceledEvent;
import com.mdh.common.waiting.domain.event.WaitingPostponedEvent;
import com.mdh.user.waiting.application.dto.UserWaitingResponse;
import com.mdh.user.waiting.application.dto.WaitingDetailsResponse;
import com.mdh.common.waiting.domain.Waiting;
import com.mdh.common.waiting.domain.WaitingPeople;
import com.mdh.common.waiting.domain.WaitingStatus;
import com.mdh.common.waiting.domain.event.WaitingCanceledEvent;
import com.mdh.common.waiting.domain.event.WaitingCreatedEvent;
import com.mdh.common.waiting.persistence.ShopWaitingRepository;
import com.mdh.common.waiting.persistence.WaitingLine;
import com.mdh.common.waiting.persistence.WaitingRepository;
import com.mdh.user.waiting.application.dto.UserWaitingResponse;
import com.mdh.user.waiting.application.dto.WaitingDetailsResponse;
import com.mdh.user.waiting.presentation.dto.WaitingCreateRequest;
import io.micrometer.core.annotation.Counted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final ShopWaitingRepository shopWaitingRepository;
    private final WaitingServiceValidator waitingServiceValidator;
    private final WaitingLine waitingLine;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public WaitingDetailsResponse findWaitingDetails(Long waitingId) {
        var waitingDetails = waitingRepository.findByWaitingDetails(waitingId)
            .orElseThrow(() -> new NoSuchElementException("해당되는 웨이팅이 없습니다. waitingId = " + waitingId));
        var shopId = waitingDetails.shopId();
        var createdDate = waitingDetails.createdDate();

        var rank = waitingLine.findRank(shopId, waitingId, createdDate);
        return new WaitingDetailsResponse(waitingDetails, rank.orElse(null));
    }

    @Counted("user.waiting.register")
    @Transactional
    public Long createWaiting(Long userId, Long shopId, WaitingCreateRequest waitingCreateRequest) {
        var shopWaiting = shopWaitingRepository.findById(shopId)
            .orElseThrow(() -> new IllegalStateException("해당 매장에 웨이팅 정보가 존재하지 않습니다. shopId : " + shopId));

        if (waitingServiceValidator.isExistsWaiting(userId)) {
            throw new IllegalStateException("해당 매장에 이미 웨이팅이 등록되어있다면 웨이팅을 추가로 등록 할 수 없다. userId : " + userId);
        }

        shopWaiting.addWaitingCount();

        var waitingPeople = createWaitingPeople(waitingCreateRequest);

        var waiting = Waiting.builder()
            .shopWaiting(shopWaiting)
            .waitingPeople(waitingPeople)
            .userId(userId)
            .build();

        var savedWaiting = waitingRepository.save(waiting); // 저장
        saveWaitingLine(shopId, savedWaiting);

        log.info("매장에 웨이팅이 등록되었습니다. {}", shopId);
        eventPublisher.publishEvent(new WaitingCreatedEvent(savedWaiting));

        return savedWaiting.getId();
    }

    @Counted("user.waiting.cancel")
    @Transactional
    public void cancelWaiting(Long waitingId) {
        var waiting = waitingRepository.findById(waitingId)
            .orElseThrow(() -> new NoSuchElementException("등록된 웨이팅이 존재하지 않습니다. waitingId : " + waitingId));

        var shopId = waiting.getShopWaiting().getShopId();

        waitingLine.cancel(shopId, waitingId, waiting.getIssuedTime());
        waiting.changeWaitingStatus(WaitingStatus.CANCEL);

        log.info("웨이팅이 취소되었습니다. {}", waitingId);
        eventPublisher.publishEvent(new WaitingCanceledEvent(waiting));
    }

    @Counted("user.waiting.postpone")
    @Transactional
    public void postPoneWaiting(Long waitingId) {
        var waiting = waitingRepository.findById(waitingId)
            .orElseThrow(() -> new NoSuchElementException("등록된 웨이팅이 존재하지 않습니다. waitingId : " + waitingId));
        var preIssuedTime = waiting.getIssuedTime();
        var shopId = waiting.getShopWaiting().getShopId();

        waitingLine.postpone(shopId, waitingId, preIssuedTime, waiting.getIssuedTime());

        log.info("웨이팅이 미루어졌습니다. {}", waitingId);
        waiting.addPostponedCount();
        eventPublisher.publishEvent(new WaitingPostponedEvent(waiting));
    }

    @Transactional(readOnly = true)
    public List<UserWaitingResponse> findAllByUserIdAndStatus(Long userId, WaitingStatus waitingStatus) {
        return waitingRepository.findAllByUserIdAndWaitingStatus(userId, waitingStatus)
            .stream()
            .map(UserWaitingResponse::new)
            .toList();
    }

    private void saveWaitingLine(Long shopId, Waiting savedWaiting) {
        var waitingId = savedWaiting.getId();
        var issuedTime = savedWaiting.getIssuedTime();
        waitingLine.save(shopId, waitingId, issuedTime); // 웨이팅 라인 저장
    }

    private WaitingPeople createWaitingPeople(WaitingCreateRequest waitingCreateRequest) {
        var adultCount = waitingCreateRequest.adultCount();
        var childCount = waitingCreateRequest.childCount();
        return new WaitingPeople(adultCount, childCount);
    }
}