package com.mdh.devtable.waiting.domain;

import com.mdh.devtable.DataInitializerFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WaitingTest {

    @ParameterizedTest
    @MethodSource("waitingPeople")
    @DisplayName("웨이팅 인원은 최소 인원 이상이고 최대 인원 이하일때 웨이팅 상태가 PROGRESS이고 미루기 횟수가 0인 상태로 생성된다.")
    void watingPeopleBetweenMinAndMax(WaitingPeople waitingPeople) {
        //given
        var shopId = 1L;
        var userId = 1L;
        var maximumWaiting = 10;
        var minimumWaitingPeople = 2;
        var maximumWaitingPeople = 5;

        var shopWaiting = DataInitializerFactory.shopWaiting(shopId, maximumWaiting, maximumWaitingPeople, minimumWaitingPeople);

        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);

        //when
        var waiting = DataInitializerFactory.waiting(userId, shopWaiting, waitingPeople);

        //then
        assertThat(waiting)
                .extracting(Waiting::getWaitingPeople,
                        Waiting::getShopWaiting,
                        Waiting::getUserId,
                        Waiting::getWaitingStatus,
                        Waiting::getPostponedCount)
                .containsExactly(waitingPeople,
                        shopWaiting,
                        userId,
                        WaitingStatus.PROGRESS,
                        0);
    }

    static Stream<Arguments> waitingPeople() {
        return Stream.of(
                Arguments.arguments(DataInitializerFactory.waitingPeople(5, 0)),
                Arguments.arguments(DataInitializerFactory.waitingPeople(5, 0))
        );
    }

    @ParameterizedTest
    @EnumSource(value = ShopWaitingStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"OPEN"})
    @DisplayName("매장 상태가 OPEN이 아닐 때 Waiting을 생성하면 예외가 발생한다.")
    public void waitingConstructorExTest(ShopWaitingStatus waitingStatus) {
        //given
        var shopId = 1L;
        var userId = 1L;
        var maximumWaiting = 10;
        var minimumWaitingPeople = 2;
        var maximumWaitingPeople = 5;

        var shopWaiting = DataInitializerFactory.shopWaiting(shopId, maximumWaiting, maximumWaitingPeople, minimumWaitingPeople);

        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);
        shopWaiting.changeShopWaitingStatus(waitingStatus);

        var waitingPeople = DataInitializerFactory.waitingPeople(2, 0);

        //when & then
        assertThatThrownBy(() -> Waiting.builder()
                .shopWaiting(shopWaiting)
                .userId(userId)
                .waitingPeople(waitingPeople)
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("매장이 오픈 상태가 아니면 웨이팅을 등록 할 수 없습니다.");
    }

    @Test
    @DisplayName("Waiting의 상태가 Progress이면 미루기 횟수를 증가 시킬 수 있다.")
    public void addWaitingPostponeCountTest() {
        //given
        var shopId = 1L;
        var userId = 1L;
        var maximumWaiting = 10;
        var minimumWaitingPeople = 2;
        var maximumWaitingPeople = 5;

        var shopWaiting = DataInitializerFactory.shopWaiting(shopId, maximumWaiting, maximumWaitingPeople, minimumWaitingPeople);

        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);

        var waitingPeople = DataInitializerFactory.waitingPeople(2, 0);

        var waiting = DataInitializerFactory.waiting(userId, shopWaiting, waitingPeople);

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
        var userId = 1L;
        var maximumWaiting = 10;
        var minimumWaitingPeople = 2;
        var maximumWaitingPeople = 5;

        var shopWaiting = DataInitializerFactory.shopWaiting(shopId, maximumWaiting, maximumWaitingPeople, minimumWaitingPeople);

        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);

        var waitingPeople = DataInitializerFactory.waitingPeople(2, 0);

        var waiting = DataInitializerFactory.waiting(userId, shopWaiting, waitingPeople);

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
        var userId = 1L;
        var maximumWaiting = 10;
        var minimumWaitingPeople = 2;
        var maximumWaitingPeople = 5;

        var shopWaiting = DataInitializerFactory.shopWaiting(shopId, maximumWaiting, maximumWaitingPeople, minimumWaitingPeople);

        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);

        var waitingPeople = DataInitializerFactory.waitingPeople(2, 0);

        var waiting = DataInitializerFactory.waiting(userId, shopWaiting, waitingPeople);

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
        //given
        var shopId = 1L;
        var userId = 1L;
        var maximumWaiting = 10;
        var minimumWaitingPeople = 2;
        var maximumWaitingPeople = 5;

        var shopWaiting = DataInitializerFactory.shopWaiting(shopId, maximumWaiting, maximumWaitingPeople, minimumWaitingPeople);

        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);

        var waitingPeople = DataInitializerFactory.waitingPeople(2, 0);

        var waiting = DataInitializerFactory.waiting(userId, shopWaiting, waitingPeople);

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
        var userId = 1L;
        var maximumWaiting = 10;
        var minimumWaitingPeople = 2;
        var maximumWaitingPeople = 5;

        var shopWaiting = DataInitializerFactory.shopWaiting(shopId, maximumWaiting, maximumWaitingPeople, minimumWaitingPeople);

        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);

        var waitingPeople = DataInitializerFactory.waitingPeople(2, 0);

        var waiting = DataInitializerFactory.waiting(userId, shopWaiting, waitingPeople);

        //when
        waiting.changeWaitingStatus(waitingStatus);

        //then
        assertThatThrownBy(() -> waiting.changeWaitingStatus(WaitingStatus.PROGRESS))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("진행 상태가 아니면 상태 변경이 불가능 합니다.");
    }

    @ParameterizedTest
    @MethodSource("waitingPeopleAndExceptionMessage")
    @DisplayName("웨이팅 인원은 최소 인원 미만이거나 최대 인원을 초과하면 예외를 던진다.")
    void waitingPeopleUnderMinExTest(WaitingPeople waitingPeople, String exceptionMessage) {
        //given
        var shopId = 1L;
        var maximumWaiting = 10;
        var minimumWaitingPeople = 2;
        var maximumWaitingPeople = 5;

        var shopWaiting = DataInitializerFactory.shopWaiting(shopId, maximumWaiting, maximumWaitingPeople, minimumWaitingPeople);

        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);

        //when&then
        assertThatThrownBy(() -> Waiting.builder()
                .shopWaiting(shopWaiting)
                .waitingPeople(waitingPeople)
                .userId(1L)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(exceptionMessage);
    }

    static Stream<Arguments> waitingPeopleAndExceptionMessage() {
        return Stream.of(
                Arguments.arguments(DataInitializerFactory.waitingPeople(1, 0), "웨이팅 인원은 2명 이상이어야 합니다."),
                Arguments.arguments(DataInitializerFactory.waitingPeople(6, 0), "웨이팅 인원은 5명 이하여야 합니다.")
        );
    }

    @Test
    @DisplayName("아동 손님을 받지 않는 매장에 아동 손님을 추가하면 예외가 발생한다.")
    void shouldThrowExceptionWhenAddingChildGuestToChildDisabledStore() {
        //given
        var shopId = 1L;
        var maximumWaiting = 10;
        var minimumWaitingPeople = 2;
        var maximumWaitingPeople = 5;

        var shopWaiting = ShopWaiting.builder()
                .shopId(shopId)
                .maximumWaiting(maximumWaiting)
                .minimumWaitingPeople(minimumWaitingPeople)
                .maximumWaitingPeople(maximumWaitingPeople)
                .build();

        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);
        shopWaiting.updateChildEnabled(false);

        //when & then
        assertThatThrownBy(() -> Waiting.builder()
                .shopWaiting(shopWaiting)
                .waitingPeople(new WaitingPeople(2, 2))
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유아 손님 입장이 불가능한 매장입니다.");
    }

    @Test
    @DisplayName("아동 손님을 받는 매장에 아동 손님을 추가할 수 있다.")
    void shouldNotThrowExceptionWhenAddingChildGuestToChildEnabledStore() {
        //given
        var shopId = 1L;
        var maximumWaiting = 10;
        var minimumWaitingPeople = 2;
        var maximumWaitingPeople = 5;

        var shopWaiting = DataInitializerFactory.shopWaiting(shopId, maximumWaiting, maximumWaitingPeople, minimumWaitingPeople);

        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);
        shopWaiting.updateChildEnabled(true);

        //when
        var waiting = DataInitializerFactory.waiting(1L, shopWaiting, DataInitializerFactory.waitingPeople(2, 2));

        //then
        assertThat(waiting).isNotNull();
    }
}