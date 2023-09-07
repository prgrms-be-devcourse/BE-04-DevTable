package com.mdh.devtable.waiting.application;

import com.mdh.devtable.waiting.infra.persistence.WaitingLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ShopWaitingServiceTest {

    @Autowired
    private ShopWaitingService shopWaitingService;

    @Autowired
    private WaitingLine waitingLine;

    @Test
    @DisplayName("특정 매장의 실시간 웨이팅 수를 확인한다.")
    void findShopTotalWaitingTest() {
        //given
        var shopId = 1L;
        var waitingId1 = 1L;
        var waitingId1Time = LocalDateTime.now();
        var waitingId2 = 2L;
        var waitingId2Time = waitingId1Time.plusMinutes(1);
        var waitingId3 = 3L;
        var waitingId3Time = waitingId2Time.plusMinutes(1);

        waitingLine.save(shopId, waitingId1, waitingId1Time);
        waitingLine.save(shopId, waitingId2, waitingId2Time);
        waitingLine.save(shopId, waitingId3, waitingId3Time);

        //when
        var shopTotalWaiting = shopWaitingService.findShopTotalWaiting(shopId);

        //then
        assertThat(shopTotalWaiting.totalWaiting()).isEqualTo(3);
    }

}