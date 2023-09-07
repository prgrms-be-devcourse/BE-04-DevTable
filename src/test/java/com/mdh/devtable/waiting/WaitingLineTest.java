package com.mdh.devtable.waiting;

import com.mdh.devtable.waiting.infra.persistence.PlainWaitingLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class WaitingLineTest {

    @Test
    @DisplayName("매장의 웨이팅에서 몇 번째 순위인지 확인한다.")
    void findRankTest() {
        //given
        var shopId = 1L;
        var waitingId1 = 1L;
        var waitingId1Time = LocalDateTime.now();
        var waitingId2 = 2L;
        var waitingId2Time = waitingId1Time.plusMinutes(1);
        var waitingId3 = 3L;
        var waitingId3Time = waitingId2Time.plusMinutes(1);

        var waitingLine = new PlainWaitingLine();
        waitingLine.save(shopId, waitingId1, waitingId1Time);
        waitingLine.save(shopId, waitingId2, waitingId2Time);
        waitingLine.save(shopId, waitingId3, waitingId3Time);

        //when
        var rank = waitingLine.findRank(shopId, waitingId3, waitingId3Time);

        //then
        assertThat(rank).isEqualTo(3);
    }

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

        var waitingLine = new PlainWaitingLine();
        waitingLine.save(shopId, waitingId1, waitingId1Time);
        waitingLine.save(shopId, waitingId2, waitingId2Time);
        waitingLine.save(shopId, waitingId3, waitingId3Time);

        //when
        var totalWaiting = waitingLine.findTotalWaiting(shopId);

        //then
        assertThat(totalWaiting).isEqualTo(3);
    }
}