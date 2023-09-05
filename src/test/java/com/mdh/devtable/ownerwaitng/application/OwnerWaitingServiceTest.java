package com.mdh.devtable.ownerwaitng.application;

import com.mdh.devtable.ownerwaitng.infra.persistence.OwnerWaitingRepository;
import com.mdh.devtable.ownerwaitng.presentaion.OwnerWaitingChangeRequest;
import com.mdh.devtable.waiting.ShopWaiting;
import com.mdh.devtable.waiting.ShopWaitingRepository;
import com.mdh.devtable.waiting.ShopWaitingStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OwnerWaitingServiceTest {

    @Autowired
    private OwnerWaitingService ownerWaitingService;

    @Autowired
    private ShopWaitingRepository shopWaitingRepository;

    @DisplayName("매장 웨이팅 상태를 변경할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"OPEN", "BREAK_TIME"})
    void changShopWaitingStatus(String status) {
        //given
        var shopWaiting = ShopWaiting
                .builder()
                .shopId(1L)
                .maximumWaitingPeople(2)
                .minimumWaitingPeople(1)
                .maximumWaiting(10)
                .build();
        shopWaitingRepository.save(shopWaiting);

        //when
        var request = new OwnerWaitingChangeRequest(status);
        ownerWaitingService.changShopWaitingStatus(shopWaiting.getShopId(), request);

        //then
        var updatedShopWaiting = shopWaitingRepository.findById(shopWaiting.getShopId()).orElseThrow();
        assertEquals(ShopWaitingStatus.valueOf(status), updatedShopWaiting.getShopWaitingStatus());
    }
}