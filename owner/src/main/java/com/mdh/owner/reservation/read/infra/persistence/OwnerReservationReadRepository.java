package com.mdh.owner.reservation.read.infra.persistence;


import com.mdh.common.reservation.domain.ReservationStatus;
import com.mdh.common.reservation.persistence.dto.OwnerShopReservationInfoResponse;

import java.util.List;

public interface OwnerReservationReadRepository {

    List<OwnerShopReservationInfoResponse> findAllReservationsByOwnerIdAndStatus(Long ownerId, ReservationStatus reservationStatus);
}