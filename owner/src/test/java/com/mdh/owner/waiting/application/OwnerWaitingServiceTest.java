package com.mdh.owner.waiting.application;

import com.mdh.owner.DataInitializerFactory;
import com.mdh.common.waiting.domain.ShopWaitingStatus;
import com.mdh.common.waiting.domain.WaitingStatus;
import com.mdh.owner.waiting.infra.persistence.OwnerWaitingRepository;
import com.mdh.common.waiting.persistence.dto.WaitingInfoResponseForOwner;
import com.mdh.owner.waiting.presentation.dto.OwnerShopWaitingStatusChangeRequest;
import com.mdh.owner.waiting.presentation.dto.OwnerUpdateShopWaitingInfoRequest;
import com.mdh.owner.waiting.presentation.dto.OwnerWaitingStatusChangeRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

    @DisplayName("점주는 매장 웨이팅 상태를 변경할 수 있다.")
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

    @DisplayName("점주는 손님의 웨이팅 상태를 변경할 수 있다.")
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

    @DisplayName("점주는 점주 id, 웨이팅 상태로 웨이팅을 조회할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"PROGRESS", "CANCEL", "NO_SHOW", "VISITED"})
    void findWaitingByShopIdAndWaitingStatus(String status) {
        var ownerId = 1L;
        var response = Collections.singletonList(new WaitingInfoResponseForOwner(1, "test"));
        var waitingStatus = WaitingStatus.valueOf(status);
        given(ownerWaitingRepository.findWaitingByOwnerIdAndWaitingStatus(ownerId, WaitingStatus.valueOf(status))).willReturn(response);

        //when
        var result = ownerWaitingService.findWaitingOwnerIdAndWaitingStatus(ownerId, waitingStatus);

        //then
        Assertions.assertThat(result).isEqualTo(response);
    }

    @DisplayName("점주는 매장의 웨이팅 정보를 변경할 수 있다.")
    @Test
    void updateShopWaitingInfo() {
        //given
        var shopId = 1L;
        var shopWaiting = DataInitializerFactory.shopWaiting(shopId, 2, 1, 1);
        var request = new OwnerUpdateShopWaitingInfoRequest(true, 3, 1, 4);
        given(ownerWaitingRepository.findShopWaitingByShopId(shopId)).willReturn(Optional.of(shopWaiting));

        //when
        ownerWaitingService.updateShopWaitingInfo(shopId, request);

        //then
        Assertions.assertThat(shopWaiting)
                .extracting("maximumWaitingPeople", "minimumWaitingPeople", "childEnabled")
                .containsExactly(request.maximumPeople(), request.minimumPeople(), request.childEnabled());
    }
}