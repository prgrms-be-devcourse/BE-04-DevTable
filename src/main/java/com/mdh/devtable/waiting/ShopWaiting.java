package com.mdh.devtable.waiting;

import com.mdh.devtable.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "shop_waitings")
@Entity
public class ShopWaiting extends BaseTimeEntity {

    @Id
    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "status", length = 31, nullable = false)
    @Enumerated(EnumType.STRING)
    private ShopWaitingStatus shopWaitingStatus;

    @Column(name = "maximum", nullable = false)
    private int maximumWaiting;

    @Builder
    public ShopWaiting(Long shopId, int maximumWaiting) {
        validMaximumWaiting(maximumWaiting);
        this.shopId = shopId;
        this.maximumWaiting = maximumWaiting;
        this.shopWaitingStatus = ShopWaitingStatus.CLOSE;
    }

    public void changeShopWaitingStatus(ShopWaitingStatus shopWaitingStatus) {
        if (this.shopWaitingStatus == shopWaitingStatus) {
            throw new IllegalStateException("매장의 웨이팅 상태를 동일한 상태로 변경 할 수 없습니다.");
        }

        this.shopWaitingStatus = shopWaitingStatus;
    }

    public void updateShopWaiting(int maximumWaiting) {
        validMaximumWaiting(maximumWaiting);
        this.maximumWaiting = maximumWaiting;
    }

    public boolean isOpenWaitingStatus() {
        return this.shopWaitingStatus == ShopWaitingStatus.OPEN;
    }

    private void validMaximumWaiting(int maximumWaiting) {
        if (maximumWaiting < 1) {
            throw new IllegalArgumentException("웨이팅의 최대 인원수는 1 미만 일 수 없습니다.");
        }
    }
}
