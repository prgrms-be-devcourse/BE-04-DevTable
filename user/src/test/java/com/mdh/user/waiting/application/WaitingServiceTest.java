package com.mdh.user.waiting.application;

import com.mdh.common.shop.domain.ShopType;
import com.mdh.common.waiting.domain.ShopWaitingStatus;
import com.mdh.common.waiting.domain.Waiting;
import com.mdh.common.waiting.domain.WaitingStatus;
import com.mdh.common.waiting.domain.event.WaitingCreatedEvent;
import com.mdh.common.waiting.persistence.ShopWaitingRepository;
import com.mdh.common.waiting.persistence.WaitingLine;
import com.mdh.common.waiting.persistence.WaitingRepository;
import com.mdh.common.waiting.persistence.dto.UserWaitingQueryDto;
import com.mdh.user.DataInitializerFactory;
import com.mdh.user.waiting.presentation.dto.MyWaitingsRequest;
import com.mdh.user.waiting.presentation.dto.WaitingCreateRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
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

    @Mock
    private ApplicationEventPublisher eventPublisher;

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
        var waitingCreatedEvent = new WaitingCreatedEvent(waiting);

        given(shopWaitingRepository.findById(any(Long.class))).willReturn(Optional.of(shopWaiting));
        given(waitingServiceValidator.isExistsWaiting(userId)).willReturn(false);
        given(waitingRepository.save(any(Waiting.class))).willReturn(waiting);
        doNothing().when(waitingLine).save(shopWaiting.getShopId(), waiting.getId(), waiting.getIssuedTime());
        doNothing().when(eventPublisher).publishEvent(waitingCreatedEvent);

        //when
        waitingService.createWaiting(waitingRequest);

        //then
        verify(shopWaitingRepository, times(1)).findById(any(Long.class));
        verify(waitingServiceValidator, times(1)).isExistsWaiting(any(Long.class));
        verify(waitingRepository, times(1)).save(any(Waiting.class));
        verify(waitingLine, times(1)).save(any(Long.class), any(), any());
        verify(eventPublisher, times(1)).publishEvent(waitingCreatedEvent);
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
        doNothing().when(waitingLine).cancel(shopId, waitingId, waiting.getIssuedTime());

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

    @ParameterizedTest
    @EnumSource(value = WaitingStatus.class)
    @DisplayName("유저는 상태별로 자신의 웨이팅을 조회 할 수 있다.")
    void findAllByUserIdAndStatusTest(WaitingStatus requestWaitingStatus) {
        //given
        var userId = 1L;
        var waitingStatus = requestWaitingStatus;
        var myWaitingsRequest = new MyWaitingsRequest(userId, waitingStatus);

        given(waitingRepository.findAllByUserIdAndWaitingStatus(myWaitingsRequest.userId(), myWaitingsRequest.waitingStatus()))
                .willReturn(List.of(new UserWaitingQueryDto(
                        1L,
                        2L,
                        "ShopName",
                        ShopType.ASIAN,
                        "City",
                        "District",
                        1,
                        2,
                        3
                )));

        //when
        var result = waitingService.findAllByUserIdAndStatus(myWaitingsRequest);

        //then
        verify(waitingRepository, times(1)).findAllByUserIdAndWaitingStatus(any(Long.class), any(WaitingStatus.class));
        assertThat(result).hasSize(1);
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

    @Test
    @DisplayName("웨이팅을 미룰 수 있다.")
    void postponeWaitingTest() {
        //given
        var waitingId = 1L;
        var shopId = 1L;
        var userId = 1L;
        var shopWaiting = DataInitializerFactory.shopWaiting(shopId, 30, 8, 2);

        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);
        var waitingPeople = DataInitializerFactory.waitingPeople(2, 0);
        var waiting = DataInitializerFactory.waiting(userId, shopWaiting, waitingPeople);
        given(waitingRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(waiting));
        given(waitingLine.isPostpone(any(Long.class), any(Long.class), any(LocalDateTime.class))).willReturn(true);

        //when
        waitingService.postPoneWaiting(waitingId);

        //then
        verify(waitingRepository, times(1)).findById(any(Long.class));
        verify(waitingLine, times(1)).postpone(
                any(Long.class),
                any(Long.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );
    }

    @Test
    @DisplayName("미루고자 하는 웨이팅이 마지막 웨이팅이라면 미룰 수 없다.")
    void postponeWaitingFailTest() {
        //given
        var waitingId = 1L;
        var shopId = 1L;
        var userId = 1L;
        var shopWaiting = DataInitializerFactory.shopWaiting(shopId, 30, 8, 2);

        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);
        var waitingPeople = DataInitializerFactory.waitingPeople(2, 0);
        var waiting = DataInitializerFactory.waiting(userId, shopWaiting, waitingPeople);
        given(waitingRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(waiting));
        given(waitingLine.isPostpone(any(Long.class), any(Long.class), any(LocalDateTime.class))).willReturn(false);

        //when & then
        Assertions.assertThatThrownBy(() -> waitingService.postPoneWaiting(waitingId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("미루기를 수행 할 수 없는 웨이팅 입니다. " + waitingId);
    }
}