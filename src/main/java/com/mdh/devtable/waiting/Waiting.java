package com.mdh.devtable.waiting;

import com.mdh.devtable.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "waitings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Waiting extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shop_id", referencedColumnName = "id")
    private ShopWaiting shopWaiting;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "waiting_status", length = 31, nullable = false)
    private WaitingStatus waitingStatus;

    @Column(name = "postponed_count", nullable = false)
    private int postponedCount;

    // 웨이팅 인원 수 최소 <= 인원 <= 최대
    @Embedded
    private WaitingPeople waitingPeople;

    @Builder
    public Waiting(ShopWaiting shopWaiting, Long userId, WaitingPeople waitingPeople) {
        if (!shopWaiting.isOpenWaitingStatus()) {
            throw new IllegalStateException("매장이 오픈 상태가 아니면 웨이팅을 등록 할 수 없습니다.");
        }
        validTotalWaitingPeople(shopWaiting, waitingPeople);
        validChildEnable(shopWaiting, waitingPeople);
        this.shopWaiting = shopWaiting;
        this.userId = userId;
        this.waitingStatus = WaitingStatus.PROGRESS;
        this.postponedCount = 0;
        this.waitingPeople = waitingPeople;
    }

    private void validChildEnable(ShopWaiting shopWaiting, WaitingPeople waitingPeople) {
        if (waitingPeople.getChildCount() > 0 && !shopWaiting.isChildEnabled()) {
            throw new IllegalArgumentException("유아 손님 입장이 불가능한 매장입니다.");
        }
    }

    private void validTotalWaitingPeople(ShopWaiting shopWaiting, WaitingPeople waitingPeople) {
        int total = waitingPeople.totalPeople();
        shopWaiting.validOverMinimumPeople(total);
        shopWaiting.validUnderMaximumPeople(total);
    }

    // 비즈니스 메서드
    public void addPostponedCount() {
        if (!isProgress()) {
            throw new IllegalStateException("진행 상태가 아닌 웨이팅 미루기는 불가능 합니다.");
        }

        if (postponedCount >= 2) {
            throw new IllegalStateException("웨이팅 미루기는 2회 초과하여 불가능 합니다.");
        }
        postponedCount++;
    }

    private boolean isProgress() {
        return this.waitingStatus == WaitingStatus.PROGRESS;
    }

    public void changeWaitingStatus(WaitingStatus waitingStatus) {
        if (!isProgress()) {
            throw new IllegalStateException("진행 상태가 아니면 상태 변경이 불가능 합니다.");
        }
        this.waitingStatus = waitingStatus;
    }

}

