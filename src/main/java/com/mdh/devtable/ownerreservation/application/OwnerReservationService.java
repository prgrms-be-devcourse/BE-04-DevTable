package com.mdh.devtable.ownerreservation.application;

import com.mdh.devtable.ownerreservation.infra.persistence.OwnerReservationRepository;
import com.mdh.devtable.ownerreservation.presentation.dto.ShopReservationCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OwnerReservationService {

    private final OwnerReservationRepository ownerReservationRepository;

    @Transactional
    public Long createShopReservation(Long shopId, ShopReservationCreateRequest shopReservationCreateRequest) {
        var shopReservation = shopReservationCreateRequest.toEntity(shopId);
        return ownerReservationRepository.saveShopReservation(shopReservation);
    }
}