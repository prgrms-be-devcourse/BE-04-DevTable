package com.mdh.devtable.reservation.persistence;

import com.mdh.devtable.reservation.Reservation;
import com.mdh.devtable.reservation.ReservationStatus;
import com.mdh.devtable.reservation.persistence.dto.OwnerShopReservationInfoResponse;
import com.mdh.devtable.reservation.persistence.dto.ReservationQueryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
            SELECT new com.mdh.devtable.reservation.persistence.dto.ReservationQueryDto(s.id, s.name, s.shopType, re.city, re.district, srdt.reservationDate, srdt.reservationTime, r.personCount, r.reservationStatus)
            FROM Reservation r
            JOIN FETCH ShopReservationDateTimeSeat srdts ON srdts.reservation.id = r.id
            JOIN ShopReservationDateTime srdt ON srdt.id = srdts.shopReservationDateTime.id
            JOIN Shop s ON s.id = r.shopReservation.shopId
            JOIN Region re ON re.id = s.region.id
            WHERE r.reservationStatus = :status AND r.userId = :userId
            """)
    List<ReservationQueryDto> findByUserIdAndReservationStatus(
            @Param("userId") Long userId,
            @Param("status") ReservationStatus reservationStatus
    );

    @Query("""
            SELECT new com.mdh.devtable.reservation.persistence.dto.OwnerShopReservationInfoResponse(
            r.requirement, srd.reservationDate, srd.reservationTime, r.reservationStatus, r.personCount, s.seatType)
            FROM Reservation r
            JOIN FETCH ShopReservationDateTimeSeat srds ON srds.reservation.id = r.id
            INNER JOIN ShopReservationDateTime srd ON srds.shopReservationDateTime.id = srd.id
            INNER JOIN Seat s ON srds.seat.id = s.id
            INNER JOIN Shop sp ON sp.id = r.shopReservation.shopId\s
            WHERE sp.userId = :userId AND r.reservationStatus = :reservationStatus
            """)
    List<OwnerShopReservationInfoResponse> findAllReservationsByOwnerIdAndStatus(@Param("userId") Long ownerId, @Param("reservationStatus") ReservationStatus reservationStatus);
}