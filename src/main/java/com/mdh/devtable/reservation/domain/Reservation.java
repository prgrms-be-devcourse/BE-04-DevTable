package com.mdh.devtable.reservation.domain;

import com.mdh.devtable.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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

    @OneToMany(mappedBy = "reservation")
    private List<ShopReservationDateTimeSeat> shopReservationDateTimeSeats = new ArrayList<>();

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

    public void addShopReservationDateTimeSeats(ShopReservationDateTimeSeat shopReservationDateTimeSeat) {
        if (!this.shopReservationDateTimeSeats.contains(shopReservationDateTimeSeat)) {
            this.shopReservationDateTimeSeats.add(shopReservationDateTimeSeat);
            shopReservationDateTimeSeat.registerReservation(this);
        }
    }

    public boolean isCancelShopReservation() {
        if (this.reservationStatus != ReservationStatus.CREATED) {
            throw new IllegalStateException("생성 상태가 아니라면 예약을 취소 할 수 없습니다.");
        }
        var yesterdayLocalDateTime = getYesterdayLocalDateTime();

        shopReservationDateTimeSeats.forEach(ShopReservationDateTimeSeat::cancelReservation);
        this.shopReservationDateTimeSeats.clear();

        this.reservationStatus = ReservationStatus.CANCEL;
        return !LocalDateTime.now()
                .isAfter(yesterdayLocalDateTime);
    }

    public void validSeatSizeAndPersonCount(int size) {
        if (size > personCount + 2) {
            throw new IllegalArgumentException("예약하려는 좌석의 수가 예약 인원 수 + 2를 초과했습니다. seats size : " + size + ", person count : " + personCount);
        }
    }

    public void updateReservationStatus(ReservationStatus reservationStatus) {
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

    private LocalDateTime getYesterdayLocalDateTime() {
        return this.shopReservationDateTimeSeats
                .get(0)
                .getShopReservationDateTime()
                .getReservationDateTime()
                .minusDays(1);
    }

    public boolean isAvailableUpdateReservation() {
        var yesterdayLocalDateTime = getYesterdayLocalDateTime();
        return !LocalDateTime.now()
                .isAfter(yesterdayLocalDateTime);
    }

    public void updateReservation(List<ShopReservationDateTimeSeat> shopReservationDateTimeSeats) {
        this.shopReservationDateTimeSeats.forEach(ShopReservationDateTimeSeat::cancelReservation);
        shopReservationDateTimeSeats.forEach(this::addShopReservationDateTimeSeats);
    }
}
