package com.mdh.devtable.reservation.infra.persistence;

import com.mdh.devtable.ownerreservation.read.application.dto.OwnerShopReservationInfoResponse;
import com.mdh.devtable.reservation.domain.Reservation;
import com.mdh.devtable.reservation.domain.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
            SELECT new com.mdh.devtable.ownerreservation.read.application.dto.OwnerShopReservationInfoResponse(
            r.requirement, srd.reservationDate, srd.reservationTime, r.reservationStatus, r.personCount, s.seatType)
            FROM Reservation r            
            JOIN FETCH ShopReservationDateTimeSeat srds ON srds.reservation.id = r.id
            INNER JOIN ShopReservationDateTime srd ON srds.shopReservationDateTime.id = srd.id
            INNER JOIN Seat s ON srds.seat.id = s.id
            INNER JOIN Shop sp ON sp.id = r.shopReservation.shopId 
            WHERE sp.userId = :userId AND r.reservationStatus = :reservationStatus
            """)
    List<OwnerShopReservationInfoResponse> findAllReservationsByOwnerIdAndStatus(@Param("userId") Long ownerId, @Param("reservationStatus") ReservationStatus reservationStatus);
}