package com.mdh.user.shop.application;

import com.mdh.user.DataInitializerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.mdh.common.shop.domain.*;
import com.mdh.common.shop.persistence.ShopRepository;
import com.mdh.user.shop.application.dto.ShopDetailInfoResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ShopServiceTest {

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
}