package com.mdh.owner.waiting.application;

import com.mdh.common.waiting.domain.Waiting;
import com.mdh.common.waiting.domain.WaitingStatus;
import com.mdh.common.waiting.domain.event.WaitingStatusChangedAsCanceledEvent;
import com.mdh.common.waiting.domain.event.WaitingStatusChangedAsVisitedEvent;
import com.mdh.owner.waiting.infra.persistence.OwnerWaitingRepository;
import com.mdh.common.waiting.persistence.dto.WaitingInfoResponseForOwner;
import com.mdh.owner.waiting.presentation.dto.OwnerShopWaitingStatusChangeRequest;
import com.mdh.owner.waiting.presentation.dto.OwnerUpdateShopWaitingInfoRequest;
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
public class OwnerWaitingService {

    private final OwnerWaitingRepository ownerWaitingRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void changeShopWaitingStatus(Long shopId, OwnerShopWaitingStatusChangeRequest request) {
        var shopWaiting = ownerWaitingRepository.findShopWaitingByShopId(shopId)
                .orElseThrow(() -> new NoSuchElementException("매장 웨이팅 조회 결과가 없습니다."));
        shopWaiting.changeShopWaitingStatus(request.shopWaitingStatus());
    }

    @Counted("owner.waiting.cancel")
    @Transactional
    public void markWaitingStatusAsCancel(Long waitingId) {
        var waiting = findWaitingByWaitingId(waitingId);
        waiting.changeWaitingStatus(WaitingStatus.CANCEL);
        eventPublisher.publishEvent(new WaitingStatusChangedAsCanceledEvent(waiting));
    }

    @Counted("owner.waiting.noShow")
    @Transactional
    public void markWaitingStatusAsNoShow(Long waitingId) {
        var waiting = findWaitingByWaitingId(waitingId);
        waiting.changeWaitingStatus(WaitingStatus.NO_SHOW);
    }

    @Counted("owner.waiting.visit")
    @Transactional
    public void markWaitingStatusAsVisited(Long waitingId) {
        var waiting = findWaitingByWaitingId(waitingId);
        waiting.changeWaitingStatus(WaitingStatus.VISITED);
        eventPublisher.publishEvent(new WaitingStatusChangedAsVisitedEvent(waiting));
    }

    private Waiting findWaitingByWaitingId(Long waitingId) {
        return ownerWaitingRepository.findWaitingByWaitingId(waitingId)
                .orElseThrow(() -> new NoSuchElementException("웨이팅 조회 결과가 없습니다. " + waitingId));
    }

    @Transactional(readOnly = true)
    public List<WaitingInfoResponseForOwner> findWaitingOwnerIdAndWaitingStatus(Long ownerId, WaitingStatus waitingStatus) {
        return ownerWaitingRepository.findWaitingByOwnerIdAndWaitingStatus(ownerId, waitingStatus);
    }

    @Transactional
    public void updateShopWaitingInfo(Long shopId, OwnerUpdateShopWaitingInfoRequest request) {
        var shopWaiting = ownerWaitingRepository.findShopWaitingByShopId(shopId)
                .orElseThrow(() -> new NoSuchElementException("매장 웨이팅 조회 결과가 없습니다."));

        shopWaiting.updateShopWaitingInfo(request.childEnabled(),
                request.maximumPeople(),
                request.minimumPeople(),
                request.maximumWaitingTeam());
    }
}