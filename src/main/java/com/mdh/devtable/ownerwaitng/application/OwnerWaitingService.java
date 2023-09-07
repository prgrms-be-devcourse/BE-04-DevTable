package com.mdh.devtable.ownerwaitng.application;

import com.mdh.devtable.ownerwaitng.infra.persistence.OwnerWaitingRepository;
import com.mdh.devtable.ownerwaitng.presentaion.OwnerWaitingChangeRequest;
import com.mdh.devtable.waiting.domain.ShopWaitingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class OwnerWaitingService {

    private final OwnerWaitingRepository ownerWaitingRepository;

    @Transactional
    public void changeShopWaitingStatus(Long shopId, OwnerWaitingChangeRequest request) {
        var shopWaiting = ownerWaitingRepository.findByShopId(shopId)
                .orElseThrow(() -> new NoSuchElementException("매장 웨이팅 조회 결과가 없습니다."));
        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.valueOf(request.waitingStatus()));
    }
}