package com.mdh.devtable.ownerreservation.read.infra.persistence;

import com.mdh.devtable.DataInitializerFactory;
import com.mdh.devtable.global.config.JpaConfig;
import com.mdh.devtable.reservation.domain.ReservationStatus;
import com.mdh.devtable.reservation.infra.persistence.*;
import com.mdh.devtable.shop.infra.persistence.RegionRepository;
import com.mdh.devtable.shop.infra.persistence.ShopRepository;
import com.mdh.devtable.user.infra.persistence.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({JpaConfig.class})
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OwnerReservationReadRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ShopReservationDateTimeRepository shopReservationDateTimeRepository;

    @Autowired
    private ShopReservationRepository shopReservationRepository;

    @Autowired
    private ShopReservationDateTimeSeatRepository shopReservationDateTimeSeatRepository;

    @DisplayName("매장 ID와 예약 상태로 모든 예약을 조회할 수 있다.")
    @Test
    void findAllReservationsByShopId() {
        //given
        var owner = DataInitializerFactory.owner();
        userRepository.save(owner);

        var region = DataInitializerFactory.region();
        regionRepository.save(region);

        var shopDetails = DataInitializerFactory.shopDetails();
        var shopAddress = DataInitializerFactory.shopAddress();
        var shop = DataInitializerFactory.shop(owner.getId(), shopDetails, region, shopAddress);
        shopRepository.save(shop);

        var shopReservation = DataInitializerFactory.shopReservation(shop.getId(), 2, 6);
        shopReservationRepository.save(shopReservation);

        var seat = DataInitializerFactory.seat(shopReservation, 4);
        seatRepository.save(seat);

        var shopReservationDateTime = DataInitializerFactory.shopReservationDateTime(shopReservation);
        shopReservationDateTimeRepository.save(shopReservationDateTime);

        var reservation = DataInitializerFactory.reservation(owner.getId(), shopReservation, 4);
        reservation.updateReservationStatus(ReservationStatus.VISITED);
        reservationRepository.save(reservation);

        var shopReservationDateTimeSeat = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime, seat);
        shopReservationDateTimeSeat.registerReservation(reservation);
        shopReservationDateTimeSeatRepository.save(shopReservationDateTimeSeat);

        //when
        var result = reservationRepository.findAllReservationsByOwnerIdAndStatus(owner.getId(), ReservationStatus.VISITED);

        //then
        assertThat(result).isNotEmpty();
        assertThat(result.get(0)).extracting("requirement", "personCount", "reservationStatus")
                .containsExactly(reservation.getRequirement(), 4, ReservationStatus.VISITED);
    }
}