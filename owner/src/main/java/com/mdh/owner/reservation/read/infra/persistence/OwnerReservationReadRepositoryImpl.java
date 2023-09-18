package com.mdh.owner.reservation.read.infra.persistence;

import com.mdh.common.reservation.domain.ReservationStatus;
import com.mdh.common.reservation.persistence.ReservationRepository;
import com.mdh.common.reservation.persistence.dto.OwnerShopReservationInfoResponse;
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