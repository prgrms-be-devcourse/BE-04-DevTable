package com.mdh.devtable.waiting.application;

import com.mdh.devtable.waiting.domain.WaitingStatus;
import com.mdh.devtable.waiting.infra.persistence.OwnerWaitingRepository;
import com.mdh.devtable.waiting.persistence.dto.WaitingInfoResponseForOwner;
import com.mdh.devtable.waiting.presentation.dto.OwnerShopWaitingStatusChangeRequest;
import com.mdh.devtable.waiting.presentation.dto.OwnerUpdateShopWaitingInfoRequest;
import com.mdh.devtable.waiting.presentation.dto.OwnerWaitingStatusChangeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

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

    @Transactional
    public void changeWaitingStatus(Long waitingId, OwnerWaitingStatusChangeRequest request) {
        var waiting = ownerWaitingRepository.findWaitingByWaitingId(waitingId)
                .orElseThrow(() -> new NoSuchElementException("웨이팅 조회 결과가 없습니다."));
        waiting.changeWaitingStatus(request.waitingStatus());
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