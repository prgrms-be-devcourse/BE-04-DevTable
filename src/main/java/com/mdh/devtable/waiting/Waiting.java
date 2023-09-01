package com.mdh.devtable.waiting;

import com.mdh.devtable.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Table(name = "waiting")
@Entity
public class Waiting extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "waiting_status", length = 31, nullable = false)
    private WaitingStatus waitingStatus;

    @Column(name = "postponed_count", nullable = false)
    private int postponedCount;

    public Waiting() {
        this.waitingStatus = WaitingStatus.PROGRESS;
        this.postponedCount = 0;
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

