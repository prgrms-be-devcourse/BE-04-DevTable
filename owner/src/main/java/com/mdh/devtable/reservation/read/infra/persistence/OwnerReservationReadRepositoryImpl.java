package com.mdh.devtable.reservation.read.infra.persistence;

import com.mdh.devtable.reservation.ReservationStatus;
import com.mdh.devtable.reservation.persistence.ReservationRepository;
import com.mdh.devtable.reservation.persistence.dto.OwnerShopReservationInfoResponse;
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