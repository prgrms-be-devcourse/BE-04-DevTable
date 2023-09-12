package com.mdh.devtable.ownershop.application;

import com.mdh.devtable.ownershop.infra.persistence.OwnerShopRepository;
import com.mdh.devtable.ownershop.presentation.dto.OwnerShopCreateRequest;
import com.mdh.devtable.ownershop.presentation.dto.RegionRequest;
import com.mdh.devtable.ownershop.presentation.dto.ShopAddressRequest;
import com.mdh.devtable.ownershop.presentation.dto.ShopDetailsRequest;
import com.mdh.devtable.shop.Shop;
import com.mdh.devtable.shop.ShopType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
        verify(ownerShopRepository, times(1)).save(any(Shop.class));
        assertThat(savedShopId).isNotNull();
    }
}