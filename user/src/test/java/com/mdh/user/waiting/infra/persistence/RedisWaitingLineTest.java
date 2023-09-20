package com.mdh.user.waiting.infra.persistence;

import com.mdh.common.waiting.persistence.WaitingLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class RedisWaitingLineTest {

    @Autowired
    private WaitingLine waitingLine;

    @Test
    @DisplayName("웨이팅 순위를 조회한다.")
    void findRankTest() {
        // given
        var shopId = 1L;

        var waitingId1 = 1L;
        var waitingId2 = 2L;
        var waitingId3 = 3L;


        var issuedDate1 = LocalDateTime.now();
        var issuedDate2 = issuedDate1.plusMinutes(1);
        var issuedDate3 = issuedDate2.plusMinutes(1);

        waitingLine.save(shopId, waitingId1, issuedDate1);
        waitingLine.save(shopId, waitingId2, issuedDate2);
        waitingLine.save(shopId, waitingId3, issuedDate3);

        // when
        long rank = waitingLine.findRank(shopId, waitingId3, issuedDate3);

        // then
        assertThat(rank).isEqualTo(3);
    }

    @Test
    @DisplayName("매장의 웨이팅 수를 조회한다.")
    void findTotalWaitingTest() {
        // given
        var shopId = 1L;

        var waitingId1 = 1L;
        var waitingId2 = 2L;
        var waitingId3 = 3L;

        var issuedDate1 = LocalDateTime.now();
        var issuedDate2 = issuedDate1.plusMinutes(1);
        var issuedDate3 = issuedDate2.plusMinutes(1);

        waitingLine.save(shopId, waitingId1, issuedDate1);
        waitingLine.save(shopId, waitingId2, issuedDate2);
        waitingLine.save(shopId, waitingId3, issuedDate3);

        // when
        long totalWaiting = waitingLine.findTotalWaiting(shopId);

        // then
        assertThat(totalWaiting).isEqualTo(3);
    }

    @Test
    @DisplayName("웨이팅을 취소한다.")
    void cancelTest() {
        // given
        var shopId = 1L;

        var waitingId1 = 1L;
        var waitingId2 = 2L;
        var waitingId3 = 3L;


        var issuedDate1 = LocalDateTime.now();
        var issuedDate2 = issuedDate1.plusMinutes(1);
        var issuedDate3 = issuedDate2.plusMinutes(1);

        waitingLine.save(shopId, waitingId1, issuedDate1);
        waitingLine.save(shopId, waitingId2, issuedDate2);
        waitingLine.save(shopId, waitingId3, issuedDate3);

        // when
        waitingLine.cancel(shopId, waitingId2, issuedDate2);

        // then
        assertThatThrownBy(() -> waitingLine.findRank(shopId, waitingId2, issuedDate2))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("매장에 해당 웨이팅이 존재하지 않습니다. waitingId " + waitingId2);
    }

    @Test
    @DisplayName("웨이팅을 미룬다.")
    void postponedTest() {
        // given
        var shopId = 1L;

        var waitingId1 = 1L;
        var waitingId2 = 2L;
        var waitingId3 = 3L;

        var issuedDate1 = LocalDateTime.now();
        var issuedDate2 = issuedDate1.plusMinutes(1);
        var issuedDate3 = issuedDate2.plusMinutes(1);

        waitingLine.save(shopId, waitingId1, issuedDate1);
        waitingLine.save(shopId, waitingId2, issuedDate2);
        waitingLine.save(shopId, waitingId3, issuedDate3);

        var postponedDate = issuedDate3.plusMinutes(1);

        // when
        waitingLine.postpone(shopId, waitingId2, issuedDate2, postponedDate);

        // then
        long rank = waitingLine.findRank(shopId, waitingId2, postponedDate);
        assertThat(rank).isEqualTo(3);
    }

    @Test
    @DisplayName("맨 뒤에 있는 웨이팅은 미룰 수 없다.")
    void postponeWaitingFailTest() {
        //given
        var shopId = 1L;
        var waitingId1 = 1L;
        var waitingId1Time = LocalDateTime.now();
        var waitingId2 = 2L;
        var waitingId2Time = waitingId1Time.plusMinutes(1);
        var waitingId3 = 3L;
        var waitingId3Time = waitingId2Time.plusMinutes(2);

        var waitingLine = new PlainWaitingLine();
        waitingLine.save(shopId, waitingId1, waitingId1Time);
        waitingLine.save(shopId, waitingId2, waitingId2Time);
        waitingLine.save(shopId, waitingId3, waitingId3Time);

        //when & then
        assertThatThrownBy(() -> waitingLine.postpone(shopId, waitingId3, waitingId3Time, LocalDateTime.now()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("미루기를 수행 할 수 없는 웨이팅 입니다. " + waitingId3);
    }
}