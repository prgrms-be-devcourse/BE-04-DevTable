package com.mdh.devtable.waiting;

import com.mdh.devtable.waiting.domain.ShopWaiting;
import com.mdh.devtable.waiting.domain.ShopWaitingStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShopWaitingTest {

    @Test
    @DisplayName("매장의 웨이팅 정보를 생성 할 수 있다.")
    void createShopWaitingTest() {
        //given
        var shopId = 1L;
        var maximumWaiting = 10;

        //when
        var shopWaiting = ShopWaiting
            .builder()
            .shopId(shopId)
            .maximumWaitingPeople(2)
            .minimumWaitingPeople(1)
            .maximumWaiting(maximumWaiting)
            .build();

        //then
        assertThat(shopWaiting.getShopId()).isEqualTo(shopId);
        assertThat(shopWaiting.getShopWaitingStatus()).isEqualTo(ShopWaitingStatus.CLOSE);
        assertThat(shopWaiting.getMaximumWaiting()).isEqualTo(maximumWaiting);
    }

    @Test
    @DisplayName("매장의 웨이팅 정보를 생성 시 최대 허용 인원수가 1 미만이면 안된다.")
    void createShopWaitingExTest() {
        //given
        var shopId = 1L;
        var maximumWaiting = 0;

        //when & then
        assertThatThrownBy(() -> ShopWaiting.builder()
            .shopId(shopId)
            .maximumWaiting(maximumWaiting)
            .build())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("웨이팅의 최대 인원수는 1 미만 일 수 없습니다.");
    }

    @ParameterizedTest
    @EnumSource(value = ShopWaitingStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"CLOSE"})
    @DisplayName("매장의 웨이팅 상태를 다른 상태로 업데이트 할 수 있다.")
    void updateShopWaitingStatusTest(ShopWaitingStatus shopWaitingStatus) {
        //given
        var shopId = 1L;
        var maximumWaiting = 10;

        var shopWaiting = ShopWaiting
            .builder()
            .shopId(shopId)
            .maximumWaiting(maximumWaiting)
            .build();

        //when
        shopWaiting.changeShopWaitingStatus(shopWaitingStatus);

        //then
        assertThat(shopWaiting.getShopWaitingStatus()).isEqualTo(shopWaitingStatus);
    }

    @Test
    @DisplayName("매장의 웨이팅 상태를 같은 상태로 업데이트 할 수 없다.")
    void updateShopWaitingStatusExTest() {
        //given
        var shopId = 1L;
        var maximumWaiting = 10;

        var shopWaiting = ShopWaiting
            .builder()
            .shopId(shopId)
            .maximumWaiting(maximumWaiting)
            .build();

        //when & then
        assertThatThrownBy(() -> shopWaiting.changeShopWaitingStatus(shopWaiting.getShopWaitingStatus()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("매장의 웨이팅 상태를 동일한 상태로 변경 할 수 없습니다.");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, Integer.MAX_VALUE})
    @DisplayName("매장의 웨이팅 허용 인원 수를 설정 할 수 있다.")
    void updateShopWaitingMaximumTest(int changeMaximumWaiting) {
        //given
        var shopId = 1L;
        var maximumWaiting = 10;

        var shopWaiting = ShopWaiting
            .builder()
            .shopId(shopId)
            .maximumWaiting(maximumWaiting)
            .build();

        //when
        shopWaiting.updateShopWaiting(changeMaximumWaiting);

        //then
        assertThat(shopWaiting.getMaximumWaiting()).isEqualTo(changeMaximumWaiting);
    }

    @Test
    @DisplayName("매장의 웨이팅 허용 인원 수를 1 미만으로 설정 할 수 없다.")
    void updateShopWaitingMaximumExTest() {
        //given
        var shopId = 1L;
        var maximumWaiting = 10;

        var shopWaiting = ShopWaiting
            .builder()
            .shopId(shopId)
            .maximumWaiting(maximumWaiting)
            .build();

        //when & then
        assertThatThrownBy(() -> shopWaiting.updateShopWaiting(0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("웨이팅의 최대 인원수는 1 미만 일 수 없습니다.");
    }

    @Test
    @DisplayName("매장의 유아 가능 여부를 설정할 수 있다.")
    void updateChildEnabled() {
        // given
        var shopId = 1L;
        var maximumWaiting = 5;
        var shopWaiting = ShopWaiting.builder()
            .shopId(shopId)
            .maximumWaiting(maximumWaiting)
            .build();

        // when
        var newChildEnabled = true;
        shopWaiting.updateChildEnabled(newChildEnabled);

        // then
        assertTrue(shopWaiting.isChildEnabled());
    }
}