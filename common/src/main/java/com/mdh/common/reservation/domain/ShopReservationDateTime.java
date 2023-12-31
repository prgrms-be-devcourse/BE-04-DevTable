package com.mdh.common.reservation.domain;

import com.mdh.common.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "shop_reservation_datetimes")
@Entity
public class ShopReservationDateTime extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private ShopReservation shopReservation;

    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;

    @Column(name = "reservation_time", nullable = false)
    private LocalTime reservationTime;

    public ShopReservationDateTime(ShopReservation shopReservation,
                                   LocalDate reservationDate,
                                   LocalTime reservationTime) {
        this.shopReservation = shopReservation;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
    }

    public LocalDateTime getReservationDateTime() {
        return LocalDateTime.of(this.reservationDate, this.reservationTime);
    }
}