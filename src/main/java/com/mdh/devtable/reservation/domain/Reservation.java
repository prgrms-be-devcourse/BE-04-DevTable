package com.mdh.devtable.reservation.domain;

import com.mdh.devtable.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reservations")
@Entity
public class Reservation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private ShopReservation shopReservation;

    @Column(name = "requirement", length = 255, nullable = true)
    private String requirement;

    @Column(name = "person_count", nullable = false)
    private int personCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 15, nullable = false)
    private ReservationStatus reservationStatus;

    @Builder
    public Reservation(
            Long userId,
            ShopReservation shopReservation,
            String requirement,
            int personCount
    ) {
        shopReservation.validPersonCount(personCount);
        this.userId = userId;
        this.shopReservation = shopReservation;
        this.requirement = requirement;
        this.personCount = personCount;
        this.reservationStatus = ReservationStatus.CREATED;
    }

    public void validSeatSizeAndPersonCount(int size) {
        if (size > personCount) {
            throw new IllegalArgumentException("예약하려는 좌석의 수가 예약 인원 수를 초과했습니다. seats size : " + size + ", person count : " + personCount);
        }
    }

    public void updateReservation(ReservationStatus reservationStatus) {
        validUpdateReservation(reservationStatus);
        this.reservationStatus = reservationStatus;
    }

    private void validUpdateReservation(ReservationStatus reservationStatus) {
        if (this.reservationStatus == reservationStatus) {
            throw new IllegalStateException("예약 상태를 동일한 상태로 변경 할 수 없습니다.");
        }

        if (!this.reservationStatus.isCreated()) {
            throw new IllegalStateException("예약이 CREATED 상태에서만 상태변경이 가능합니다.");
        }
    }
}
