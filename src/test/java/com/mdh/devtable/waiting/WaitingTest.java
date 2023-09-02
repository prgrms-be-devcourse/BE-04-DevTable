package com.mdh.devtable.waiting;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WaitingTest {

    @Test
    @DisplayName("Waiting을 생성하면 웨이팅 상태가 PROGRESS이고, 미루기 횟수는 0이여야 한다.")
    public void waitingConstructorTest() {
        //given
        var shopId = 1L;
        var maximumWaiting = 10;
        var userId = 1L;

        var shopWaiting = ShopWaiting
                .builder()
                .shopId(shopId)
                .maximumWaiting(maximumWaiting)
                .build();

        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);
        //when
        var waiting = Waiting.builder()
                .shopWaiting(shopWaiting)
                .userId(userId)
                .build();

        //then
        assertThat(waiting.getShopWaiting()).isEqualTo(shopWaiting);
        assertThat(waiting.getUserId()).isEqualTo(userId);
        assertThat(waiting.getPostponedCount()).isEqualTo(0);
        assertThat(waiting.getWaitingStatus()).isEqualTo(WaitingStatus.PROGRESS);
    }

    @ParameterizedTest
    @EnumSource(value = ShopWaitingStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"OPEN"})
    @DisplayName("매장 상태가 OPEN이 아닐 때 Waiting을 생성하면 예외가 발생한다.")
    public void waitingConstructorExTest(ShopWaitingStatus waitingStatus) {
        //given
        var shopId = 1L;
        var maximumWaiting = 10;
        var userId = 1L;

        var shopWaiting = ShopWaiting
                .builder()
                .shopId(shopId)
                .maximumWaiting(maximumWaiting)
                .build();
        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);
        shopWaiting.changeShopWaitingStatus(waitingStatus);

        //when & then
        assertThatThrownBy(() -> Waiting.builder()
                .shopWaiting(shopWaiting)
                .userId(userId)
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("매장이 오픈 상태가 아니면 웨이팅을 등록 할 수 없습니다.");
    }

    @Test
    @DisplayName("Waiting의 상태가 Progress이면 미루기 횟수를 증가 시킬 수 있다.")
    public void addWaitingPostponeCountTest() {
        //given
        var shopId = 1L;
        var maximumWaiting = 10;
        var userId = 1L;

        var shopWaiting = ShopWaiting
                .builder()
                .shopId(shopId)
                .maximumWaiting(maximumWaiting)
                .build();

        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);

        var waiting = Waiting.builder()
                .shopWaiting(shopWaiting)
                .userId(userId)
                .build();
        //when
        waiting.addPostponedCount();

        //then
        assertThat(waiting.getWaitingStatus()).isEqualTo(WaitingStatus.PROGRESS);
        assertThat(waiting.getPostponedCount()).isEqualTo(1);
    }

    @ParameterizedTest
    @EnumSource(value = WaitingStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"PROGRESS"})
    @DisplayName("Waiting의 상태가 Progress가 아니면 미루기 횟수를 증가 시킬 수 없다.")
    public void addWaitingPostponeCountStatusExTest(WaitingStatus waitingStatus) {
        //given
        var shopId = 1L;
        var maximumWaiting = 10;
        var userId = 1L;

        var shopWaiting = ShopWaiting
                .builder()
                .shopId(shopId)
                .maximumWaiting(maximumWaiting)
                .build();

        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);

        var waiting = Waiting.builder()
                .shopWaiting(shopWaiting)
                .userId(userId)
                .build();

        //when
        waiting.changeWaitingStatus(waitingStatus);

        //then
        Assertions.assertThatThrownBy(waiting::addPostponedCount)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("진행 상태가 아닌 웨이팅 미루기는 불가능 합니다.");
    }

    @Test
    @DisplayName("Waiting의 미루기 횟수가 2회 일 때, 미루기 횟수를 증가시키면 예외가 발생한다.")
    public void addWaitingPostponeCountExTest() {
        //given
        var shopId = 1L;
        var maximumWaiting = 10;
        var userId = 1L;

        var shopWaiting = ShopWaiting
                .builder()
                .shopId(shopId)
                .maximumWaiting(maximumWaiting)
                .build();

        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);

        var waiting = Waiting.builder()
                .shopWaiting(shopWaiting)
                .userId(userId)
                .build();

        //when
        waiting.addPostponedCount();
        waiting.addPostponedCount();

        //then
        Assertions.assertThatThrownBy(waiting::addPostponedCount)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("웨이팅 미루기는 2회 초과하여 불가능 합니다.");
    }

    @ParameterizedTest
    @EnumSource(value = WaitingStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"PROGRESS"})
    @DisplayName("Waiting의 상태가 PROGRESS라면 다른 상태로 변경이 가능하다.")
    public void changeWaitingStatusTest(WaitingStatus waitingStatus) {
        //given
        var shopId = 1L;
        var maximumWaiting = 10;
        var userId = 1L;

        var shopWaiting = ShopWaiting
                .builder()
                .shopId(shopId)
                .maximumWaiting(maximumWaiting)
                .build();

        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);

        var waiting = Waiting.builder()
                .shopWaiting(shopWaiting)
                .userId(userId)
                .build();

        //when
        waiting.changeWaitingStatus(waitingStatus);

        //then
        assertThat(waiting.getWaitingStatus()).isEqualTo(waitingStatus);
    }

    @ParameterizedTest
    @EnumSource(value = WaitingStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"PROGRESS"})
    @DisplayName("Waiting의 상태가 PROGRESS가 아니라면 다른 상태로 변경이 불가능하다.")
    public void changeWaitingStatusExTest(WaitingStatus waitingStatus) {
        //given
        var shopId = 1L;
        var maximumWaiting = 10;
        var userId = 1L;

        var shopWaiting = ShopWaiting
                .builder()
                .shopId(shopId)
                .maximumWaiting(maximumWaiting)
                .build();

        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);

        var waiting = Waiting.builder()
                .shopWaiting(shopWaiting)
                .userId(userId)
                .build();

        //when
        waiting.changeWaitingStatus(waitingStatus);

        //then
        assertThatThrownBy(() -> waiting.changeWaitingStatus(WaitingStatus.PROGRESS))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("진행 상태가 아니면 상태 변경이 불가능 합니다.");
    }
}