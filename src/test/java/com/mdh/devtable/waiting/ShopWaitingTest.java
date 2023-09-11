package com.mdh.devtable.waiting;

import com.mdh.devtable.DataInitializerFactory;
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
        var shopWaiting = DataInitializerFactory.shopWaiting(shopId, maximumWaiting, 2, 1);

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

        var shopWaiting = DataInitializerFactory.shopWaiting(shopId, maximumWaiting, 2, 1);

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

        var shopWaiting = DataInitializerFactory.shopWaiting(shopId, maximumWaiting, 2, 1);

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

        var shopWaiting = DataInitializerFactory.shopWaiting(shopId, maximumWaiting, 2, 1);

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

        var shopWaiting = DataInitializerFactory.shopWaiting(shopId, maximumWaiting, 2, 1);

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
        var maximumWaiting = 10;

        var shopWaiting = DataInitializerFactory.shopWaiting(shopId, maximumWaiting, 2, 1);

        // when
        var newChildEnabled = true;
        shopWaiting.updateChildEnabled(newChildEnabled);

        // then
        assertTrue(shopWaiting.isChildEnabled());
    }

    @Test
    @DisplayName("매장의 상태가 CLOSE가 되면 매장의 발급번호가 0이 된다.")
    void initShopWaitingCountTest() {
        //given
        var shopId = 1L;
        var maximumWaiting = 10;

        var shopWaiting = DataInitializerFactory.shopWaiting(shopId, maximumWaiting, 2, 1);

        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);

        //when
        shopWaiting.addWaitingCount();

        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.CLOSE);

        //then
        assertThat(shopWaiting.getWaitingCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("매장의 상태가 CLOSE라면 매장의 발급번호 증가 시 예외가 발생한다.")
    void addShopWaitingCountExTest() {
        //given
        var shopId = 1L;
        var maximumWaiting = 10;

        var shopWaiting = DataInitializerFactory.shopWaiting(shopId, maximumWaiting, 2, 1);

        //when & then
        assertThatThrownBy(shopWaiting::addWaitingCount)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("닫혀있는 상태에서는 발급번호 개수가 증가할 수 없습니다.");
    }

    @DisplayName("매장의 웨이팅 정보를 변경할 수 있다.")
    @Test
    void updateShopWaitingInfo() {
        //given
        var shopId = 1L;
        var maximumWaiting = 10;
        var shopWaiting = DataInitializerFactory.shopWaiting(shopId, maximumWaiting, 2, 1);
        boolean childEnabled = true;
        var maximumWaitingPeople = 2;
        var minimumWaitingPeople = 1;

        //when
        shopWaiting.updateShopWaitingInfo(childEnabled, maximumWaitingPeople, minimumWaitingPeople, maximumWaiting);

        //then
        assertThat(shopWaiting)
                .extracting("maximumWaitingPeople",
                        "minimumWaitingPeople",
                        "childEnabled",
                        "maximumWaiting")
                .containsExactly(maximumWaitingPeople, minimumWaitingPeople, childEnabled, maximumWaiting);
    }
}