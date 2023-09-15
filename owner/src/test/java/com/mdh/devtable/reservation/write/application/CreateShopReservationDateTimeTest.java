package com.mdh.devtable.reservation.write.application;

import com.mdh.devtable.DataInitializerFactory;
import com.mdh.devtable.reservation.SeatStatus;
import com.mdh.devtable.reservation.ShopReservationDateTime;
import com.mdh.devtable.reservation.ShopReservationDateTimeSeat;
import com.mdh.devtable.reservation.persistence.SeatRepository;
import com.mdh.devtable.reservation.persistence.ShopReservationDateTimeRepository;
import com.mdh.devtable.reservation.persistence.ShopReservationDateTimeSeatRepository;
import com.mdh.devtable.reservation.persistence.ShopReservationRepository;
import com.mdh.devtable.reservation.write.presentation.dto.ShopReservationDateTimeCreateRequest;
import com.mdh.devtable.shop.persistence.RegionRepository;
import com.mdh.devtable.shop.persistence.ShopRepository;
import com.mdh.devtable.user.persistence.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class CreateShopReservationDateTimeTest {

    @Autowired
    private OwnerReservationService ownerReservationService;

    @Autowired
    private ShopReservationDateTimeRepository shopReservationDateTimeRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ShopReservationRepository shopReservationRepository;

    @Autowired
    private ShopReservationDateTimeSeatRepository shopReservationDateTimeSeatRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RegionRepository regionRepository;

    @DisplayName("점주가 매장의 예약날짜시간을 등록할 때 예약날짜시간좌석도 함께 등록된다.")
    @Test
    void createShopReservationDateTime() {
        // given
        var owner = DataInitializerFactory.owner();
        userRepository.save(owner);

        var region = DataInitializerFactory.region();
        regionRepository.save(region);

        var shopAddress = DataInitializerFactory.shopAddress();
        var shopDetails = DataInitializerFactory.shopDetails();
        var shop = DataInitializerFactory.shop(owner.getId(), shopDetails, region, shopAddress);
        shopRepository.save(shop);

        var shopReservation = DataInitializerFactory.shopReservation(shop.getId(), 1, 5);
        shopReservationRepository.save(shopReservation);

        var seat = DataInitializerFactory.seat(shopReservation);
        seatRepository.save(seat);

        var formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        var formattedTime = LocalTime.parse(LocalTime.now().format(formatter));

        var shopReservationDateTimeCreateRequest = new ShopReservationDateTimeCreateRequest(LocalDate.now(), formattedTime);

        // when
        Long shopReservationDateTimeId = ownerReservationService.createShopReservationDateTime(shop.getId(), shopReservationDateTimeCreateRequest);

        // then
        var savedShopReservationDateTime = shopReservationDateTimeRepository.findById(shopReservationDateTimeId).orElse(null);
        assertThat(savedShopReservationDateTime)
                .isNotNull()
                .extracting(ShopReservationDateTime::getReservationDate, ShopReservationDateTime::getReservationTime)
                .containsExactly(shopReservationDateTimeCreateRequest.localDate(), shopReservationDateTimeCreateRequest.localTime());

        var savedShopReservationDateTimeSeats = shopReservationDateTimeSeatRepository.findAllByShopReservationDateTimeId(savedShopReservationDateTime.getId());
        assertThat(savedShopReservationDateTimeSeats)
                .isNotEmpty()
                .extracting(ShopReservationDateTimeSeat::getSeatStatus)
                .contains(SeatStatus.AVAILABLE);
    }
}