package com.mdh.owner.shop.application;

import com.mdh.owner.DataInitializerFactory;
import com.mdh.common.shop.domain.Shop;
import com.mdh.common.shop.domain.ShopType;
import com.mdh.owner.shop.infra.persistence.OwnerShopRepository;
import com.mdh.owner.shop.presentation.dto.OwnerShopCreateRequest;
import com.mdh.owner.shop.presentation.dto.RegionRequest;
import com.mdh.owner.shop.presentation.dto.ShopAddressRequest;
import com.mdh.owner.shop.presentation.dto.ShopDetailsRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class OwnerShopServiceTest {

    @InjectMocks
    private OwnerShopService ownerShopService;

    @Mock
    private OwnerShopRepository ownerShopRepository;


    @DisplayName("점주는 새로운 매장을 생성할 수 있다.")
    @Test
    void createShop() {
        // Given
        var userId = 1L;
        var shopDetails = DataInitializerFactory.shopDetails();
        var region = DataInitializerFactory.region();
        var shopAddress = DataInitializerFactory.shopAddress();
        var shop = DataInitializerFactory.shop(userId, shopDetails, region, shopAddress);
        var ownerShopCreateRequest = new OwnerShopCreateRequest(
                "test",
                "Test Shop",
                ShopType.ASIAN,
                new ShopDetailsRequest("Introduce", "9-5", "Info", "www.test.com", "123-456", "Sunday"),
                new ShopAddressRequest("123 Test St", "12345", "37.123", "127.123"),
                new RegionRequest("city", "district")
        );
        given(ownerShopRepository.save(any(Shop.class))).willReturn(1L);

        // When
        Long savedShopId = ownerShopService.createShop(userId, ownerShopCreateRequest);

        // Then
        assertThat(savedShopId).isNotNull();
    }
}