package com.mdh.devtable.ownerreservation.application;

import com.mdh.devtable.ownerreservation.infra.persistence.OwnerReservationRepository;
import com.mdh.devtable.ownerreservation.presentation.dto.ShopReservationCreateRequest;
import com.mdh.devtable.reservation.domain.ShopReservation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerReservationServiceTest {

    @InjectMocks
    private OwnerReservationService ownerReservationService;

    @Mock
    private OwnerReservationRepository ownerReservationRepository;


    @Test
    void createShopReservation() {
        // given
        Long shopId = 1L;
        var request = new ShopReservationCreateRequest(5, 1);
        when(ownerReservationRepository.saveShopReservation(any(ShopReservation.class))).thenReturn(1L);

        // when
        Long result = ownerReservationService.createShopReservation(shopId, request);

        // then
        assertThat(result).isEqualTo(1L);
        verify(ownerReservationRepository, times(1)).saveShopReservation(any(ShopReservation.class));
    }
}