package com.mdh.owner.waiting.application;

import com.mdh.common.waiting.domain.ShopWaitingStatus;
import com.mdh.owner.waiting.application.OwnerWaitingService;
import com.mdh.owner.waiting.presentation.dto.OwnerShopWaitingStatusChangeRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


@Disabled
@ActiveProfiles("lock")
@SpringBootTest
@Transactional
class OwnerWaitingLockTest {

    @Autowired
    private OwnerWaitingService ownerWaitingService;

    @DisplayName("매장의 웨이팅 상태 변경 동시에 30개 요청시 일관성 유지")
    @Test
    void ChangeWaitingStatusWithOptimisticLockTest() throws InterruptedException {
        //given
        var shopId = 1L; // 예시로 사용되는 매장 ID data.sql로 db에 shopWaiting row 하나 추가해줘야함
        var threadCount = 30;
        var executorService = Executors.newFixedThreadPool(threadCount);
        var countDownLatch = new CountDownLatch(threadCount);
        var successCount = new AtomicInteger(0);
        var failedCount = new AtomicInteger(0);
        var lockConflictCount = new AtomicInteger(0);

        //when
        for (int i = 0; i < threadCount; i++) {
            final var threadNumber = i;
            executorService.submit(() -> {
                try {
                    ShopWaitingStatus status;
                    if (threadNumber == threadCount - 1) {
                        status = ShopWaitingStatus.OPEN;
                    } else if (threadNumber % 3 == 0) {
                        status = ShopWaitingStatus.OPEN;
                    } else if (threadNumber % 3 == 1) {
                        status = ShopWaitingStatus.CLOSE;
                    } else {
                        status = ShopWaitingStatus.BREAK_TIME;
                    }
                    ownerWaitingService.changeShopWaitingStatus(shopId, new OwnerShopWaitingStatusChangeRequest(status));
                    successCount.incrementAndGet();
                } catch (ObjectOptimisticLockingFailureException e) {
                    lockConflictCount.incrementAndGet();
                } catch (IllegalStateException e) {
                    failedCount.incrementAndGet();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        //then
        var totalTrial = successCount.get() + failedCount.get() + lockConflictCount.get();
        Assertions.assertThat(totalTrial).isEqualTo(threadCount);
    }
}