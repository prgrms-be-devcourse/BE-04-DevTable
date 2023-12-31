package com.mdh.common.reservation.domain;

import com.mdh.common.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


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

    @Transient
    private UUID reservationId;

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
        this.reservationId = UUID.randomUUID();
        this.reservationStatus = ReservationStatus.CREATED;
    }

    public Reservation(
        UUID reservationId,
        Long userId,
        String requirement,
        int personCount
    ) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.requirement = requirement;
        this.personCount = personCount;
        this.reservationStatus = ReservationStatus.CREATED;
    }

    public void addShopReservation(ShopReservation shopReservation) {
        shopReservation.validPersonCount(this.personCount);
        this.shopReservation = shopReservation;
    }

    public void addShopReservationDateTimeSeats(List<ShopReservationDateTimeSeat> shopReservationDateTimeSeats) {
        shopReservationDateTimeSeats.stream()
            .filter((shopReservationDateTimeSeat) -> !this.shopReservationDateTimeSeats.contains(shopReservationDateTimeSeat))
            .forEach((shopReservationDateTimeSeat -> {
                    this.shopReservationDateTimeSeats.add(shopReservationDateTimeSeat);
                    shopReservationDateTimeSeat.registerReservation(this);
                })
            );
    }

    public boolean isCancelShopReservation() {
        if (this.reservationStatus != ReservationStatus.CREATED) {
            throw new IllegalStateException("생성 상태가 아니라면 예약을 취소 할 수 없습니다.");
        }
        var yesterdayLocalDateTime = getYesterdayLocalDateTime();

        shopReservationDateTimeSeats.forEach(ShopReservationDateTimeSeat::cancelReservation);
        this.shopReservationDateTimeSeats.clear();

        this.reservationStatus = ReservationStatus.CANCEL;
        return !isAfterYesterday(yesterdayLocalDateTime);
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

    public void updateReservation(List<ShopReservationDateTimeSeat> shopReservationDateTimeSeats) {
        validAvailableUpdateReservation();
        this.shopReservationDateTimeSeats.forEach(ShopReservationDateTimeSeat::cancelReservation);
        addShopReservationDateTimeSeats(shopReservationDateTimeSeats);
    }

    private void validAvailableUpdateReservation() {
        var yesterdayLocalDateTime = getYesterdayLocalDateTime();

        if (isAfterYesterday(yesterdayLocalDateTime)) {
            throw new IllegalStateException("예약이 24시간 이내로 남은 경우 예약 수정이 불가능합니다. reservationId : " + id);
        }
    }

    private boolean isAfterYesterday(LocalDateTime yesterdayLocalDateTime) {
        return LocalDateTime.now().isAfter(yesterdayLocalDateTime);
    }
}