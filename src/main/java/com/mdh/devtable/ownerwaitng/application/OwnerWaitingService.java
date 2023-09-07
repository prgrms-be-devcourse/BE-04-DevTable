package com.mdh.devtable.ownerwaitng.application;

import com.mdh.devtable.ownerwaitng.infra.persistence.OwnerWaitingRepository;
import com.mdh.devtable.ownerwaitng.presentaion.dto.OwnerShopWaitingStatusChangeRequest;
import com.mdh.devtable.ownerwaitng.presentaion.dto.OwnerWaitingStatusChangeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}