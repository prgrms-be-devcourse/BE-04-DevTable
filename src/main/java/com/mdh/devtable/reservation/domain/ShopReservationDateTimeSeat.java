package com.mdh.devtable.reservation.domain;

import com.mdh.devtable.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "shop_reservation_datetime_seats")
@Entity
public class ShopReservationDateTimeSeat extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shop_reservation_datetime_id", nullable = false)
    private ShopReservationDateTime shopReservationDateTime;

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = true)
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_status", length = 15, nullable = false)
    private SeatStatus seatStatus;

    public ShopReservationDateTimeSeat(ShopReservationDateTime shopReservationDateTime,
                                       Seat seat) {
        this.shopReservationDateTime = shopReservationDateTime;
        this.seat = seat;
        this.reservation = null;
        this.seatStatus = SeatStatus.AVAILABLE;
    }

    public void registerReservation(Reservation reservation) {
        if (seatStatus.isUnavailable()) {
            throw new IllegalStateException("예약된 좌석은 다시 예약할 수 없습니다.");
        }
        this.reservation = reservation;
        this.seatStatus = SeatStatus.UNAVAILABLE;
    }

    public void cancelReservation() {
        if (seatStatus.isAvailable()) {
            throw new IllegalStateException("예약 가능한 좌석이므로 취소할 수 없습니다.");
        }
        this.reservation = null;
        this.seatStatus = SeatStatus.AVAILABLE;
    }
}