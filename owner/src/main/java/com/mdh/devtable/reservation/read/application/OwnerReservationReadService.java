package com.mdh.devtable.reservation.read.application;

import com.mdh.devtable.reservation.ReservationStatus;
import com.mdh.devtable.reservation.persistence.dto.OwnerShopReservationInfoResponse;
import com.mdh.devtable.reservation.read.infra.persistence.OwnerReservationReadRepository;
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