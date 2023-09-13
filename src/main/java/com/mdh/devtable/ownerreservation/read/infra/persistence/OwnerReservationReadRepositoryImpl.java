package com.mdh.devtable.ownerreservation.read.infra.persistence;

import com.mdh.devtable.ownerreservation.read.application.dto.OwnerShopReservationInfoResponse;
import com.mdh.devtable.reservation.domain.ReservationStatus;
import com.mdh.devtable.reservation.infra.persistence.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class OwnerReservationReadRepositoryImpl implements OwnerReservationReadRepository {

    private final ReservationRepository reservationRepository;

    @Override
    public List<OwnerShopReservationInfoResponse> findAllReservationsByOwnerIdAndStatus(Long ownerId, ReservationStatus reservationStatus) {
        return reservationRepository.findAllReservationsByOwnerIdAndStatus(ownerId, reservationStatus);
    }
}