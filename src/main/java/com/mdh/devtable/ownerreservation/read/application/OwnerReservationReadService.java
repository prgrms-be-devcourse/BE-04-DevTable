package com.mdh.devtable.ownerreservation.read.application;

import com.mdh.devtable.ownerreservation.read.application.dto.OwnerShopReservationInfoResponse;
import com.mdh.devtable.ownerreservation.read.infra.persistence.OwnerReservationReadRepository;
import com.mdh.devtable.reservation.domain.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OwnerReservationReadService {

    private final OwnerReservationReadRepository ownerReservationReadRepository;

    @Transactional(readOnly = true)
    public List<OwnerShopReservationInfoResponse> findAllReservationsByOwnerIdAndStatus(Long ownerId, ReservationStatus reservationStatus) {
        return ownerReservationReadRepository.findAllReservationsByOwnerIdAndStatus(ownerId, reservationStatus);
    }

}