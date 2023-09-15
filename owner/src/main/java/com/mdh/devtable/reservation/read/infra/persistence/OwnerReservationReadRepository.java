package com.mdh.devtable.reservation.read.infra.persistence;


import com.mdh.devtable.reservation.ReservationStatus;
import com.mdh.devtable.reservation.persistence.dto.OwnerShopReservationInfoResponse;

import java.util.List;

public interface OwnerReservationReadRepository {

    List<OwnerShopReservationInfoResponse> findAllReservationsByOwnerIdAndStatus(Long ownerId, ReservationStatus reservationStatus);
}