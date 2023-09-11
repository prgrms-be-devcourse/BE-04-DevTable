package com.mdh.devtable.waiting;

import com.mdh.devtable.waiting.infra.persistence.PlainWaitingLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @DisplayName("매장에서 웨이팅이 취소가 되면 순위가 변경된다.")
    void cancelWaitingTest() {
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
        waitingLine.cancel(shopId, waitingId2, waitingId2Time);

        //then
        var rank = waitingLine.findRank(shopId, waitingId3, waitingId3Time);
        assertThat(rank).isEqualTo(2);
    }

    @Test
    @DisplayName("현재 매장에 등록된 웨이팅 정보가 없다면 예외가 발생한다.")
    void cancelWaitingExTest() {
        //given
        var shopId = 1L;
        var waitingId = 1L;
        var waitingTime = LocalDateTime.now();

        var waitingLine = new PlainWaitingLine();
        //when & then
        assertThatThrownBy(() -> waitingLine.cancel(shopId, waitingId, waitingTime))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("현재 매장에 등록 된 웨이팅이 아닙니다. shopId : " + shopId + "waitingId : " + waitingId);
    }

    @Test
    @DisplayName("매장에 등록된 웨이팅을 맨 뒤로 미룰 수 있다.")
    void postponeWaitingTest() {
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

        //when
        waitingLine.postpone(shopId, waitingId1, waitingId1Time, waitingId3Time.plusMinutes(1));
        var findWaiting = waitingLine.findRank(shopId, waitingId1, waitingId3Time.plusMinutes(1));

        //then
        assertThat(findWaiting).isEqualTo(3);
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

        //when
        var isPostpone = waitingLine.isPostpone(shopId, waitingId3, waitingId3Time);

        //then
        assertThat(isPostpone).isEqualTo(false);
    }
}