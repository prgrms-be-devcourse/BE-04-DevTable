package com.mdh.common.shop.persistence;

import com.mdh.common.DataInitializerFactory;
import com.mdh.common.reservation.persistence.*;
import com.mdh.common.user.persistence.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ShopRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ShopReservationRepository shopReservationRepository;

    @Autowired
    private ShopReservationDateTimeRepository shopReservationDateTimeRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ShopReservationDateTimeSeatRepository shopReservationDateTimeSeatRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    /**
     * 2023.9.17 오후 7시 예약 가능한 식당 찾음
     */
    @Test
    @DisplayName("특정 날짜와 시간에 예약 가능한 테이블 수로 매장을 정렬한다.")
    void findAvailableReservationShopTest() {
        //given
        shopsAndShopReservations();

        var pageRequest = PageRequest.of(0, 2, Sort.by("createdDate").ascending());

        //when
        var availableReservationShop = shopRepository.searchReservationShopByFilter(pageRequest,
                LocalDate.of(2023, 9, 17),
                LocalTime.of(19, 0, 0),
                null,
                null,
                null,
                null);

        //then
        assertThat(availableReservationShop.getTotalElements()).isEqualTo(3);
        assertThat(availableReservationShop.getTotalPages()).isEqualTo(2);
        assertThat(availableReservationShop.getContent()).hasSize(2);
        assertThat(availableReservationShop.getContent())
                .extracting("availableSeatCount")
                .contains(2, 1);
    }

    @Test
    @DisplayName("특정 시간과 날짜에 예약 가능한 매장을 인원을 기준으로 필터 조회한다.")
    void findAvailableReservationShopPersonFilterTest() {
        //given
        shopsAndShopReservations();

        var pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Order.asc("createdDate")));

        //when
        var reservationAvailableShop = shopRepository.searchReservationShopByFilter(pageRequest,
                LocalDate.of(2023, 9, 17),
                LocalTime.of(19, 0, 0),
                6,
                null,
                null,
                null);

        //then
        assertThat(reservationAvailableShop.getTotalElements()).isEqualTo(1);
        assertThat(reservationAvailableShop.getTotalPages()).isEqualTo(1);
        assertThat(reservationAvailableShop.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("특정 시간과 날짜에 예약 가능한 매장을 지역을 기준으로 필터 조회한다.")
    void findAvailableReservationShopRegionFilterTest() {
        //given
        shopsAndShopReservations();

        var pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Order.asc("createdDate")));

        //when
        var reservationAvailableShop = shopRepository.searchReservationShopByFilter(pageRequest,
                LocalDate.of(2023, 9, 17),
                LocalTime.of(19, 0, 0),
                null,
                "서울",
                null,
                null);

        //then
        assertThat(reservationAvailableShop.getTotalElements()).isEqualTo(3);
        assertThat(reservationAvailableShop.getTotalPages()).isEqualTo(2);
        assertThat(reservationAvailableShop.getContent()).hasSize(2);
    }

    /**
     * shop1 -> 2023.9.17 오후 7시 예약 가능 테이블 1개, (서울, 강남구), 인원 2-5
     * shop2 -> 2023.9.17 오후 7시 예약 불가능, (서울, 청담), 인원 2-5
     * shop3 -> 2023.9.17 오후 8시 예약 가능, (서울, 강남구), 인원 2-7
     * shop4 -> 2023.9.17 오후 7시 예약 가능 테이블 2개, (서울, 청담), 인원 2-7
     */
    private void shopsAndShopReservations() {
        var owner = DataInitializerFactory.owner();
        var guest = DataInitializerFactory.guest();
        userRepository.saveAll(List.of(owner, guest));

        var region1 = DataInitializerFactory.region("서울", "강남구");
        var region2 = DataInitializerFactory.region("서울", "청담");
        regionRepository.saveAll(List.of(region1, region2));

        var shopDetails = DataInitializerFactory.shopDetails();
        var shopAddress = DataInitializerFactory.shopAddress();

        var shop1 = DataInitializerFactory.shop(owner.getId(), shopDetails, region1, shopAddress);
        var shop2 = DataInitializerFactory.shop(owner.getId(), shopDetails, region2, shopAddress);
        var shop3 = DataInitializerFactory.shop(owner.getId(), shopDetails, region1, shopAddress);
        var shop4 = DataInitializerFactory.shop(owner.getId(), shopDetails, region2, shopAddress);
        shopRepository.saveAll(List.of(shop1, shop2, shop3, shop4));

        var shopReservation1 = DataInitializerFactory.shopReservation(shop1.getId(), 2, 5);
        var shopReservation2 = DataInitializerFactory.shopReservation(shop2.getId(), 2, 5);
        var shopReservation3 = DataInitializerFactory.shopReservation(shop3.getId(), 2, 7);
        var shopReservation4 = DataInitializerFactory.shopReservation(shop4.getId(), 2, 7);
        shopReservationRepository.saveAll(List.of(shopReservation1, shopReservation2, shopReservation3, shopReservation4));

        var reservationDate = LocalDate.of(2023, 9, 17);
        var reservationTime1 = LocalTime.of(19, 0, 0);
        var reservationTime2 = LocalTime.of(20, 0, 0);

        var shopReservationDateTime1 = DataInitializerFactory.shopReservationDateTime(shopReservation1, reservationDate, reservationTime1);
        var shopReservationDateTime2 = DataInitializerFactory.shopReservationDateTime(shopReservation2, reservationDate, reservationTime1);
        var shopReservationDateTime3 = DataInitializerFactory.shopReservationDateTime(shopReservation3, reservationDate, reservationTime2);
        var shopReservationDateTime4 = DataInitializerFactory.shopReservationDateTime(shopReservation4, reservationDate, reservationTime1);
        shopReservationDateTimeRepository.saveAll(List.of(shopReservationDateTime1, shopReservationDateTime2, shopReservationDateTime3, shopReservationDateTime4));

        var seat1 = DataInitializerFactory.seat(shopReservation1);
        var seat2 = DataInitializerFactory.seat(shopReservation2);
        var seat3 = DataInitializerFactory.seat(shopReservation3);
        var seat4 = DataInitializerFactory.seat(shopReservation4);
        var seat5 = DataInitializerFactory.seat(shopReservation4);
        seatRepository.saveAll(List.of(seat1, seat2, seat3, seat4, seat5));

        var shopReservationDateTimeSeat1 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime1, seat1);
        var shopReservationDateTimeSeat2 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime2, seat2);
        var shopReservationDateTimeSeat3 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime3, seat3);
        var shopReservationDateTimeSeat4 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime4, seat4);
        var shopReservationDateTimeSeat5 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime4, seat5);
        shopReservationDateTimeSeatRepository.saveAll(List.of(shopReservationDateTimeSeat1, shopReservationDateTimeSeat2, shopReservationDateTimeSeat3, shopReservationDateTimeSeat4, shopReservationDateTimeSeat5));

        var reservation = DataInitializerFactory.reservation(guest.getId(), shopReservation2, 4);
        reservationRepository.save(reservation);

        shopReservationDateTimeSeat2.registerReservation(reservation);
    }
}