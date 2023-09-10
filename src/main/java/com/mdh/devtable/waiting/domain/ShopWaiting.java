package com.mdh.devtable.waiting.domain;

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

    @Column(name = "waiting_count", nullable = false)
    private int waitingCount;

    @Column(name = "status", length = 31, nullable = false)
    @Enumerated(EnumType.STRING)
    private ShopWaitingStatus shopWaitingStatus;

    @Column(name = "maximum", nullable = false) // 최대 웨이팅 팀 수
    private int maximumWaiting;

    @Column(name = "child_enabled", nullable = false)
    private boolean childEnabled;
    // 최소 웨이팅 인원, 최대 웨이팅 인원
    @Column(name = "minimum_people", nullable = false)
    private int minimumWaitingPeople;

    @Column(name = "maximum_people", nullable = false)
    private int maximumWaitingPeople;

    @Version
    private Long version;

    @Builder
    public ShopWaiting(Long shopId,
                       int maximumWaiting,
                       int minimumWaitingPeople,
                       int maximumWaitingPeople) {
        validMaximumWaiting(maximumWaiting);
        this.shopId = shopId;
        this.waitingCount = 0;
        this.maximumWaiting = maximumWaiting;
        this.shopWaitingStatus = ShopWaitingStatus.CLOSE;
        this.childEnabled = false;
        this.minimumWaitingPeople = minimumWaitingPeople;
        this.maximumWaitingPeople = maximumWaitingPeople;
    }

    private void validMaximumWaiting(int maximumWaiting) {
        if (maximumWaiting < 1) {
            throw new IllegalArgumentException("웨이팅의 최대 인원수는 1 미만 일 수 없습니다.");
        }
    }

    public void updateShopWaiting(int maximumWaiting) {
        validMaximumWaiting(maximumWaiting);
        this.maximumWaiting = maximumWaiting;
    }

    public void changeShopWaitingStatus(ShopWaitingStatus shopWaitingStatus) {
        if (this.shopWaitingStatus == shopWaitingStatus) {
            throw new IllegalStateException("매장의 웨이팅 상태를 동일한 상태로 변경 할 수 없습니다.");
        }

        if (shopWaitingStatus.isCloseWaitingStatus()) {
            this.waitingCount = 0;
        }

        this.shopWaitingStatus = shopWaitingStatus;
    }

    public boolean isOpenWaitingStatus() {
        return this.shopWaitingStatus == ShopWaitingStatus.OPEN;
    }

    public void addWaitingCount() {
        if (this.shopWaitingStatus.isCloseWaitingStatus()) {
            throw new IllegalStateException("닫혀있는 상태에서는 발급번호 개수가 증가할 수 없습니다.");
        }

        this.waitingCount++;
    }

    public void updateChildEnabled(boolean childEnabled) {
        this.childEnabled = childEnabled;
    }

    public void validOverMinimumPeople(int people) {
        if (people < minimumWaitingPeople) {
            throw new IllegalArgumentException("웨이팅 인원은 " + minimumWaitingPeople + "명 이상이어야 합니다.");
        }
    }

    public void validUnderMaximumPeople(int people) {
        if (people > maximumWaitingPeople) {
            throw new IllegalArgumentException("웨이팅 인원은 " + maximumWaitingPeople + "명 이하여야 합니다.");
        }
    }

    public void updateShopWaitingInfo(boolean childEnabled,
                                      int maximumWaitingPeople,
                                      int minimumWaitingPeople,
                                      int maximumWaiting) {
        this.childEnabled = childEnabled;
        this.maximumWaitingPeople = maximumWaitingPeople;
        this.minimumWaitingPeople = minimumWaitingPeople;
        this.maximumWaiting = maximumWaiting;
    }
}