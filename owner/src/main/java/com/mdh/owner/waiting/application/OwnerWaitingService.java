package com.mdh.owner.waiting.application;

import com.mdh.common.waiting.domain.WaitingStatus;
import com.mdh.common.waiting.persistence.dto.WaitingInfoResponseForOwner;
import com.mdh.owner.waiting.infra.persistence.OwnerWaitingRepository;
import com.mdh.owner.waiting.presentation.dto.OwnerShopWaitingStatusChangeRequest;
import com.mdh.owner.waiting.presentation.dto.OwnerUpdateShopWaitingInfoRequest;
import com.mdh.owner.waiting.presentation.dto.OwnerWaitingStatusChangeRequest;
import io.micrometer.core.annotation.Counted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class OwnerWaitingService {

    private final OwnerWaitingRepository ownerWaitingRepository;

    @Transactional
    public void changeShopWaitingStatus(Long shopId, OwnerShopWaitingStatusChangeRequest request) {
        var shopWaiting = ownerWaitingRepository.findShopWaitingByShopId(shopId)
                .orElseThrow(() -> new NoSuchElementException("매장 웨이팅 조회 결과가 없습니다."));
        shopWaiting.changeShopWaitingStatus(request.shopWaitingStatus());
    }

    @Counted("owner.waiting.change")
    @Transactional
    public void changeWaitingStatus(Long waitingId, OwnerWaitingStatusChangeRequest request) {
        var waiting = ownerWaitingRepository.findWaitingByWaitingId(waitingId)
                .orElseThrow(() -> new NoSuchElementException("웨이팅 조회 결과가 없습니다."));

        waiting.changeWaitingStatus(request.waitingStatus());
        log.info("웨이팅의 상태를 {}로 변경하였습니다.", request.waitingStatus().name());
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