package com.mdh.devtable.waiting;

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
        var waitingId2Time = LocalDateTime.now().plusMinutes(5);

        var waitingLine = new WaitingLine();
        waitingLine.save(shopId, waitingId1, waitingId1Time);
        waitingLine.save(shopId, waitingId2, waitingId2Time);

        //when
        var rank = waitingLine.findRank(shopId, waitingId2, waitingId2Time);

        //then
        assertThat(rank).isEqualTo(2);
    }
}