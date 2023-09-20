package com.mdh.user.shop.application;

import com.mdh.common.shop.domain.ShopType;
import com.mdh.common.shop.persistence.ShopRepository;
import com.mdh.common.shop.persistence.dto.ReservationShopSearchQueryDto;
import com.mdh.user.DataInitializerFactory;
import com.mdh.user.shop.application.dto.ShopDetailInfoResponse;
import com.mdh.user.shop.presentation.dto.ReservationShopSearchRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ShopServiceTest {

    @InjectMocks
    private ShopService shopService;

    @Mock
    private ShopRepository shopRepository;

    @DisplayName("매장의 아이디로 매장 상세 정보를 조회할 수 있다.")
    @Test
    public void findShopDetailsById_ShouldReturnShopDetailInfoResponse_WhenShopExists() {
        // Given
        var shopId = 1L;
        var shopDetails = DataInitializerFactory.shopDetails();
        var region = DataInitializerFactory.region();
        var shopAddress = DataInitializerFactory.shopAddress();
        var mockShop = DataInitializerFactory.shop(1L, shopDetails, region, shopAddress);

        given(shopRepository.findById(shopId)).willReturn(Optional.of(mockShop));

        // When
        ShopDetailInfoResponse result = shopService.findShopDetailsById(shopId);

        // Then
        assertThat(result)
                .isNotNull()
                .extracting(
                        ShopDetailInfoResponse::name,
                        ShopDetailInfoResponse::description,
                        ShopDetailInfoResponse::shopType
                )
                .contains(
                        mockShop.getName(),
                        mockShop.getDescription(),
                        mockShop.getShopType()
                );

        assertThat(result.shopDetails())
                .isNotNull()
                .extracting(
                        ShopDetailInfoResponse.ShopDetailsResponse::introduce,
                        ShopDetailInfoResponse.ShopDetailsResponse::openingHour,
                        ShopDetailInfoResponse.ShopDetailsResponse::info
                )
                .contains(
                        mockShop.getShopDetails().getIntroduce(),
                        mockShop.getShopDetails().getOpeningHours(),
                        mockShop.getShopDetails().getInfo()
                );
    }

    @Test
    @DisplayName("예약할 매장들을 필터 조회한다.")
    void searchReservationShop() {
        //given
        var pageSize = 16;
        var total = 50;
        var pageRequest = PageRequest.of(0, pageSize, Sort.by("createdDate").ascending());
        var request = new ReservationShopSearchRequest(LocalDate.of(2023, 9, 17),
                LocalTime.of(19, 0, 0),
                4,
                "서울",
                10000,
                20000);
        var shopPrice = DataInitializerFactory.shopPrice();
        var reservationShopSearchQueryDto1 = new ReservationShopSearchQueryDto(1L,
                "식당 이름",
                "식당 설명",
                ShopType.KOREAN,
                "서울",
                "강남구",
                shopPrice,
                3);
        var reservationShopSearchQueryDto2 = new ReservationShopSearchQueryDto(2L,
                "식당 이름",
                "식당 설명",
                ShopType.AMERICAN,
                "서울",
                "강남구",
                shopPrice,
                3);
        var reservationShopQueryDtos = List.of(reservationShopSearchQueryDto1, reservationShopSearchQueryDto2);
        var page = new PageImpl<>(reservationShopQueryDtos, pageRequest, total);

        given(shopRepository.searchReservationShopByFilter(any(Pageable.class),
                any(LocalDate.class),
                any(LocalTime.class),
                anyInt(),
                anyString(),
                anyInt(),
                anyInt())).willReturn(page);

        //when
        var reservationShopSearchResponse = shopService.searchReservationShop(pageRequest, request);

        //then
        assertThat(reservationShopSearchResponse.totalPages()).isEqualTo(total / pageSize + 1);
        assertThat(reservationShopSearchResponse.hasNext()).isTrue();
        assertThat(reservationShopSearchResponse.shops()).hasSize(2);
    }
}