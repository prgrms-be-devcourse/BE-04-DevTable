package com.mdh.devtable.reservation.domain;

import com.mdh.devtable.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "seats")
@Entity
public class Seat extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private ShopReservation shopReservation;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", length = 15, nullable = false)
    private SeatType seatType;

    public Seat(ShopReservation shopReservation, SeatType seatType) {
        this.shopReservation = shopReservation;
        this.seatType = seatType;
    }
}