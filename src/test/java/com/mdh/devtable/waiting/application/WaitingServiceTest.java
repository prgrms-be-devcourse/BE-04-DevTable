package com.mdh.devtable.waiting.application;

import com.mdh.devtable.DataInitializerFactory;
import com.mdh.devtable.waiting.domain.ShopWaitingStatus;
import com.mdh.devtable.waiting.domain.Waiting;
import com.mdh.devtable.waiting.domain.WaitingStatus;
import com.mdh.devtable.waiting.infra.persistence.ShopWaitingRepository;
import com.mdh.devtable.waiting.infra.persistence.WaitingLine;
import com.mdh.devtable.waiting.infra.persistence.WaitingRepository;
import com.mdh.devtable.waiting.presentation.dto.WaitingCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WaitingServiceTest {

    @InjectMocks
    private WaitingService waitingService;

    @Mock
    private ShopWaitingRepository shopWaitingRepository;

    @Mock
    private WaitingRepository waitingRepository;

    @Mock
    private WaitingServiceValidator waitingServiceValidator;

    @Mock
    private WaitingLine waitingLine;

    @Test
    @DisplayName("웨이팅을 생성한다.")
    void createWaitingTest() {
        //given
        var userId = 1L;
        var shopId = 1L;
        var adultCount = 1;
        var childCount = 1;
        var shopWaiting = DataInitializerFactory.shopWaiting(shopId, 2, 3, 1);
        var waitingPeople = DataInitializerFactory.waitingPeople(adultCount, childCount);
        shopWaiting.updateChildEnabled(true);
        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);
        var waiting = DataInitializerFactory.waiting(userId, shopWaiting, waitingPeople);
        var waitingRequest = new WaitingCreateRequest(userId, shopId, adultCount, childCount);

        given(shopWaitingRepository.findById(any(Long.class))).willReturn(Optional.of(shopWaiting));
        given(waitingServiceValidator.isExistsWaiting(userId)).willReturn(false);
        given(waitingRepository.save(any(Waiting.class))).willReturn(waiting);
        doNothing().when(waitingLine).save(shopWaiting.getShopId(), waiting.getId(), waiting.getCreatedDate());

        //when
        waitingService.createWaiting(waitingRequest);

        //then
        verify(shopWaitingRepository, times(1)).findById(any(Long.class));
        verify(waitingServiceValidator, times(1)).isExistsWaiting(any(Long.class));
        verify(waitingRepository, times(1)).save(any(Waiting.class));
        verify(waitingLine, times(1)).save(any(Long.class), any(), any());
    }

    @Test
    @DisplayName("웨이팅을 취소한다.")
    void cancelWaitingTest() {
        //given
        var waitingId = 1L;
        var shopId = 1L;
        var shopWaiting = DataInitializerFactory.shopWaiting(shopId, 2, 3, 1);
        var waitingPeople = DataInitializerFactory.waitingPeople(1, 1);
        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);
        shopWaiting.updateChildEnabled(true);
        var waiting = DataInitializerFactory.waiting(1L, shopWaiting, waitingPeople);

        given(waitingRepository.findById(any(Long.class))).willReturn(Optional.of(waiting));
        doNothing().when(waitingLine).cancel(shopId, waitingId, waiting.getCreatedDate());

        //when
        waitingService.cancelWaiting(waitingId);

        //then
        verify(waitingRepository, times(1)).findById(any(Long.class));
        verify(waitingLine, times(1)).cancel(any(Long.class), any(), any());
        assertThat(WaitingStatus.CANCEL).isEqualTo(waiting.getWaitingStatus());
    }

    @Test
    @DisplayName("웨이팅이 등록된 상태에서 웨이팅을 추가로 등록 할 수 없다.")
    void createWaitingWithProgressingWaitingTest() {
        //given
        var userId = 1L;
        var shopId = 1L;
        var adultCount = 1;
        var childCount = 1;
        var shopWaiting = DataInitializerFactory.shopWaiting(shopId, 2, 3, 1);
        shopWaiting.updateChildEnabled(true);
        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);
        var waitingRequest = new WaitingCreateRequest(userId, shopId, adultCount, childCount);

        given(shopWaitingRepository.findById(any(Long.class))).willReturn(Optional.of(shopWaiting));
        given(waitingServiceValidator.isExistsWaiting(userId)).willReturn(true);

        //when & then
        assertThatThrownBy(() -> waitingService.createWaiting(waitingRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("해당 매장에 이미 웨이팅이 등록되어있다면 웨이팅을 추가로 등록 할 수 없다. userId : " + userId);
    }

    @Test
    @DisplayName("웨이팅이 진행 상태의 경우 상세 정보를 조회할 때 웨이팅 순위도 함께 조회된다.")
    void findWaitingDetailsProgressStatusTest() {
        //given
        var waitingId = 1L;

        var shopDetails = DataInitializerFactory.shopDetails();
        var waitingPeople = DataInitializerFactory.waitingPeople(2, 0);
        var waitingStatus = WaitingStatus.PROGRESS;
        var waitingDetails = DataInitializerFactory.waitingDetails(shopDetails, waitingStatus, waitingPeople);

        var waitingRank = 5;

        given(waitingRepository.findByWaitingDetails(any(Long.class))).willReturn(Optional.ofNullable(waitingDetails));
        given(waitingLine.findRank(any(Long.class), any(Long.class), any(LocalDateTime.class))).willReturn(waitingRank);

        //when
        var waitingDetailsResponse = waitingService.findWaitingDetails(waitingId);

        //then
        verify(waitingRepository, times(1)).findByWaitingDetails(any(Long.class));
        verify(waitingLine, times(1)).findRank(any(Long.class),
                any(Long.class),
                any(LocalDateTime.class));
        assertThat(waitingDetailsResponse.shop().shopName()).isEqualTo(waitingDetails.shopName());
        assertThat(waitingDetailsResponse.shop().shopType()).isEqualTo(waitingDetails.shopType());
        assertThat(waitingDetailsResponse.shop().region()).isEqualTo(waitingDetails.region());
        assertThat(waitingDetailsResponse.shop().shopDetails()).usingRecursiveComparison().isEqualTo(shopDetails);
        assertThat(waitingDetailsResponse.waitingNumber()).isEqualTo(waitingDetails.waitingNumber());
        assertThat(waitingDetailsResponse.waitingStatus()).isEqualTo(waitingStatus);
        assertThat(waitingDetailsResponse.waitingPeople()).usingRecursiveComparison().isEqualTo(waitingPeople);
        assertThat(waitingDetailsResponse.waitingRank()).isEqualTo(waitingRank);
        assertThat(waitingDetailsResponse.createdDate()).isEqualTo(waitingDetails.createdDate());
        assertThat(waitingDetailsResponse.modifiedDate()).isEqualTo(waitingDetails.modifiedDate());
    }

    @ParameterizedTest
    @EnumSource(value = WaitingStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"PROGRESS"})
    @DisplayName("웨이팅이 진행 상태가 아닌 경우 상세 정보를 조회할 때 웨이팅 순위는 조회되지 않는다.")
    void findWaitingDetailsTest(WaitingStatus waitingStatus) {
        //given
        var waitingId = 1L;

        var shopDetails = DataInitializerFactory.shopDetails();
        var waitingPeople = DataInitializerFactory.waitingPeople(2, 0);
        var waitingDetails = DataInitializerFactory.waitingDetails(shopDetails, waitingStatus, waitingPeople);

        given(waitingRepository.findByWaitingDetails(any(Long.class))).willReturn(Optional.ofNullable(waitingDetails));

        //when
        var waitingDetailsResponse = waitingService.findWaitingDetails(waitingId);

        //then
        verify(waitingRepository, times(1)).findByWaitingDetails(any(Long.class));
        verify(waitingLine, never()).findRank(any(Long.class),
                any(Long.class),
                any(LocalDateTime.class));
        assertThat(waitingDetailsResponse.shop().shopName()).isEqualTo(waitingDetails.shopName());
        assertThat(waitingDetailsResponse.shop().shopType()).isEqualTo(waitingDetails.shopType());
        assertThat(waitingDetailsResponse.shop().region()).isEqualTo(waitingDetails.region());
        assertThat(waitingDetailsResponse.shop().shopDetails()).usingRecursiveComparison().isEqualTo(shopDetails);
        assertThat(waitingDetailsResponse.waitingNumber()).isEqualTo(waitingDetails.waitingNumber());
        assertThat(waitingDetailsResponse.waitingStatus()).isEqualTo(waitingStatus);
        assertThat(waitingDetailsResponse.waitingPeople()).usingRecursiveComparison().isEqualTo(waitingPeople);
        assertThat(waitingDetailsResponse.waitingRank()).isNull();
        assertThat(waitingDetailsResponse.createdDate()).isEqualTo(waitingDetails.createdDate());
        assertThat(waitingDetailsResponse.modifiedDate()).isEqualTo(waitingDetails.modifiedDate());
    }
}