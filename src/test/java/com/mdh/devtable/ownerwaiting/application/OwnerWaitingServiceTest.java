package com.mdh.devtable.ownerwaiting.application;

import com.mdh.devtable.DataInitializerFactory;
import com.mdh.devtable.ownerwaiting.application.dto.WaitingInfoResponseForOwner;
import com.mdh.devtable.ownerwaiting.infra.persistence.OwnerWaitingRepository;
import com.mdh.devtable.ownerwaiting.presentaion.dto.OwnerShopWaitingStatusChangeRequest;
import com.mdh.devtable.ownerwaiting.presentaion.dto.OwnerWaitingStatusChangeRequest;
import com.mdh.devtable.ownerwaiting.presentaion.dto.WaitingInfoRequestForOwner;
import com.mdh.devtable.waiting.domain.ShopWaitingStatus;
import com.mdh.devtable.waiting.domain.WaitingStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
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
        var shopWaiting = DataInitializerFactory.shopWaiting(shopId, 2, 1, 1);
        var request = new OwnerShopWaitingStatusChangeRequest(ShopWaitingStatus.valueOf(status));
        given(ownerWaitingRepository.findShopWaitingByShopId(shopId)).willReturn(Optional.of(shopWaiting));

        // when
        ownerWaitingService.changeShopWaitingStatus(shopId, request);

        // then
        verify(ownerWaitingRepository, times(1)).findShopWaitingByShopId(shopId);
        Assertions.assertThat(ShopWaitingStatus.valueOf(status)).isEqualTo(shopWaiting.getShopWaitingStatus());
    }

    @DisplayName("손님의 웨이팅 상태를 변경할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"PROGRESS", "CANCEL", "NO_SHOW", "VISITED"})
    void changeWaitingStatus(String status) {
        //given
        var shopWaiting = DataInitializerFactory.shopWaiting(1L, 2, 1, 1);
        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);
        var waitingId = 1L;
        var waitingPeople = DataInitializerFactory.waitingPeople(1, 0);
        var waiting = DataInitializerFactory.waiting(1L, shopWaiting, waitingPeople);

        var request = new OwnerWaitingStatusChangeRequest(WaitingStatus.valueOf(status));
        given(ownerWaitingRepository.findWaitingByWaitingId(waitingId)).willReturn(Optional.of(waiting));

        // when
        ownerWaitingService.changeWaitingStatus(waitingId, request);

        // then
        verify(ownerWaitingRepository, times(1)).findWaitingByWaitingId(waitingId);
        Assertions.assertThat(WaitingStatus.valueOf(status)).isEqualTo(waiting.getWaitingStatus());
    }

    @DisplayName("점주 id, 웨이팅 상태로 웨이팅을 조회할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"PROGRESS", "CANCEL", "NO_SHOW", "VISITED"})
    void findWaitingByShopIdAndWaitingStatus(String status) {
        var ownerId = 1L;
        var response = Collections.singletonList(new WaitingInfoResponseForOwner(1, "test"));
        var request = new WaitingInfoRequestForOwner(WaitingStatus.valueOf(status));
        given(ownerWaitingRepository.findWaitingByOwnerIdAndWaitingStatus(ownerId, WaitingStatus.valueOf(status))).willReturn(response);

        //when
        var result = ownerWaitingService.findWaitingByOwnerIdAndWaitingStatus(ownerId, request);

        //then
        Assertions.assertThat(result).isEqualTo(response);
    }
}