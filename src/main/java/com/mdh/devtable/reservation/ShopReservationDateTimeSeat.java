package com.mdh.devtable.reservation;

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
    @JoinColumn(name = "shop_reservation_datetime_id")
    private ShopReservationDateTime shopReservationDateTime;

    @ManyToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_status", length = 15, nullable = false)
    private SeatStatus seatStatus;

    public ShopReservationDateTimeSeat(ShopReservationDateTime shopReservationDateTime,
                                       Seat seat) {
        this.shopReservationDateTime = shopReservationDateTime;
        this.seat = seat;
        this.seatStatus = SeatStatus.AVAILABLE;
    }

    public void changeSeatStaus(SeatStatus seatStatus) {
        if (this.seatStatus.isSameStatus(seatStatus)) {
            throw new IllegalArgumentException("같은 좌석 상태로 변경할 수 없습니다.");
        }
        this.seatStatus = seatStatus;
    }
}