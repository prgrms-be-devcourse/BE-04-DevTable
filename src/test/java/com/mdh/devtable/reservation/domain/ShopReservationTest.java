package com.mdh.devtable.reservation.domain;

import com.mdh.devtable.DataInitializerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShopReservationTest {

    @Test
    @DisplayName("매장 예약 사전 정보를 생성 할 수 있다.")
    void shopReservationTest() {
        //given
        var shopId = 1L;
        var minimumCount = 1;
        var maximumCount = 30;


        // when & then
        assertThatCode(() -> DataInitializerFactory.shopReservation(shopId, minimumCount, maximumCount))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("매장 예약 사전 정보 생성 시 최소 인원 수를 0 이하로 설정할 수 없다.")
    void shopReservationMinimumCountTest(int minimumCount) {
        //given
        var shopId = 1L;
        var maximumCount = 30;


        // when & then
        assertThatThrownBy(() -> DataInitializerFactory.shopReservation(shopId, minimumCount, maximumCount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("1 보다 작은 수로 최소 인원을 정할 수 없습니다." + minimumCount);
    }

    @Test
    @DisplayName("매장 예약 사전 정보 생성 시 최대 인원 수가 30을 초과 할 수 없습니다.")
    void shopReservationMaximumOutOfRangeTest() {
        //given
        var shopId = 1L;
        var minimumCount = 10;
        var maximumCount = 31;


        // when & then
        assertThatThrownBy(() -> DataInitializerFactory.shopReservation(shopId, minimumCount, maximumCount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("30 보다 큰 수로 최대 인원을 정할 수 없습니다." + maximumCount);
    }

    @Test
    @DisplayName("매장 예약 사전 정보 생성 시 최소 인원 수가 최대 인원 수 보다 클 수 없다.")
    void shopReservationMinimumNoLessThanMaximumTest() {
        //given
        var shopId = 1L;
        var minimumCount = 10;
        var maximumCount = 5;


        // when & then
        assertThatThrownBy(() -> DataInitializerFactory.shopReservation(shopId, minimumCount, maximumCount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("최대 인원 수보다 최소 인원 수가 더 클 수 없습니다.");
    }

}