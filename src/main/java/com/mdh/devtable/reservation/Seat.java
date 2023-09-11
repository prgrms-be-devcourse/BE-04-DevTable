package com.mdh.devtable.reservation;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", length = 15, nullable = false)
    private SeatType seatType;

    public Seat(SeatType seatType) {
        this.seatType = seatType;
    }
}