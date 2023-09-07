package com.mdh.devtable.ownerwaitng.application;

import com.mdh.devtable.ownerwaitng.infra.persistence.OwnerWaitingRepository;
import com.mdh.devtable.ownerwaitng.presentaion.dto.OwnerShopWaitingStatusChangeRequest;
import com.mdh.devtable.ownerwaitng.presentaion.dto.OwnerWaitingStatusChangeRequest;
import com.mdh.devtable.waiting.domain.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class OwnerWaitingServiceTest {

    @Mock
    private OwnerWaitingRepository ownerWaitingRepository;

    @InjectMocks
    private OwnerWaitingService ownerWaitingService;

    @DisplayName("매장 웨이팅 상태를 변경할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"OPEN", "BREAK_TIME"})
    void changeShopWaitingStatus(String status) {
        //given
        var shopId = 1L;
        var shopWaiting = ShopWaiting
                .builder()
                .shopId(shopId)
                .maximumWaitingPeople(2)
                .minimumWaitingPeople(1)
                .maximumWaiting(10)
                .build();
        var request = new OwnerShopWaitingStatusChangeRequest(ShopWaitingStatus.valueOf(status));
        given(ownerWaitingRepository.findShopWaitingByShopId(shopId)).willReturn(Optional.of(shopWaiting));

        // when
        ownerWaitingService.changeShopWaitingStatus(shopId, request);

        // then
        verify(ownerWaitingRepository, times(1)).findShopWaitingByShopId(shopId);
        Assertions.assertEquals(ShopWaitingStatus.valueOf(status), shopWaiting.getShopWaitingStatus());
    }

    @DisplayName("손님의 웨이팅 상태를 변경할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"PROGRESS", "CANCEL", "NO_SHOW", "VISITED"})
    void changeWaitingStatus(String status) {
        //given
        var shopWaiting = ShopWaiting
                .builder()
                .shopId(1L)
                .maximumWaitingPeople(2)
                .minimumWaitingPeople(1)
                .maximumWaiting(10)
                .build();
        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);
        var waitingId = 1L;
        var waiting = Waiting.builder()
                .shopWaiting(shopWaiting)
                .userId(1L)
                .waitingPeople(new WaitingPeople(1, 0))
                .build();

        var request = new OwnerWaitingStatusChangeRequest(WaitingStatus.valueOf(status));
        given(ownerWaitingRepository.findWaitingByWaitingId(waitingId)).willReturn(Optional.of(waiting));

        // when
        ownerWaitingService.changeWaitingStatus(waitingId, request);

        // then
        verify(ownerWaitingRepository, times(1)).findWaitingByWaitingId(waitingId);
        Assertions.assertEquals(WaitingStatus.valueOf(status), waiting.getWaitingStatus());
    }
}