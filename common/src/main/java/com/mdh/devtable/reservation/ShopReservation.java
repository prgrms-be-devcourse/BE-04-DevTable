package com.mdh.devtable.reservation;

import com.mdh.devtable.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "shop_reservations")
@Entity
public class ShopReservation extends BaseTimeEntity {

    @Id
    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "minimum_person", nullable = false)
    private int minimumPerson;

    @Column(name = "maximum_person", nullable = false)
    private int maximumPerson;

    public ShopReservation(Long shopId, int minimumPerson, int maximumPerson) {
        validPersonCount(minimumPerson, maximumPerson);
        this.shopId = shopId;
        this.minimumPerson = minimumPerson;
        this.maximumPerson = maximumPerson;
    }

    private void validPersonCount(int minimumPerson, int maximumPerson) {
        if (minimumPerson < 1) {
            throw new IllegalArgumentException("1 보다 작은 수로 최소 인원을 정할 수 없습니다." + minimumPerson);
        }

        if (maximumPerson > 30) {
            throw new IllegalArgumentException("30 보다 큰 수로 최대 인원을 정할 수 없습니다." + maximumPerson);
        }

        if (minimumPerson > maximumPerson) {
            throw new IllegalArgumentException("최대 인원 수보다 최소 인원 수가 더 클 수 없습니다.");
        }
    }

    public void validPersonCount(int personCount) {
        if (isOutOfRangePersonCount(personCount)) {
            throw new IllegalArgumentException("예약 인원 수[" + personCount + "]가 매장[" + shopId + "]에서 정한 " +
                    "최소 최대 인원 범위에서 벗어납니다." +
                    "[" + minimumPerson + " ~ " + maximumPerson + "]");
        }
    }

    private boolean isOutOfRangePersonCount(int personCount) {
        return this.minimumPerson > personCount || this.maximumPerson < personCount;
    }
}
