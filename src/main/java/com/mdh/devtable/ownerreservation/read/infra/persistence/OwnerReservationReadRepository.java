package com.mdh.devtable.ownerreservation.read.infra.persistence;

import com.mdh.devtable.ownerreservation.read.application.dto.OwnerShopReservationInfoResponse;
import com.mdh.devtable.reservation.domain.ReservationStatus;

import java.util.List;

public interface OwnerReservationReadRepository {

    List<OwnerShopReservationInfoResponse> findAllReservationsByOwnerIdAndStatus(Long ownerId, ReservationStatus reservationStatus);
}