package com.mdh.common.reservation.persistence;

import com.mdh.common.DataInitializerFactory;
import com.mdh.common.reservation.domain.ReservationStatus;
import com.mdh.common.reservation.domain.ShopReservationDateTimeSeat;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ReservationRepositoryTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private ReservationRepository reservationRepository;

    @ParameterizedTest
    @EnumSource(value = ReservationStatus.class)
    @DisplayName("유저가 예약했던 정보를 상태별로 조회 할 수 있다.")
    void findByUserIdAndReservationStatus(ReservationStatus status) {
        //given
        var em = entityManagerFactory.createEntityManager();
        var transaction = em.getTransaction();

        transaction.begin();
        var owner = DataInitializerFactory.owner();
        em.persist(owner);

        //매장 정보 생성
        var region = DataInitializerFactory.region();
        em.persist(region);

        var shopAddress = DataInitializerFactory.shopAddress();
        var shopDetails = DataInitializerFactory.shopDetails();

        var shop = DataInitializerFactory.shop(owner.getId(), shopDetails, region, shopAddress);
        em.persist(shop);

        var shopReservation = DataInitializerFactory.shopReservation(shop.getId(), 1, 30);
        em.persist(shopReservation);

        var seat = DataInitializerFactory.seat(shopReservation);
        em.persist(seat);

        var shopReservationDateTime1 = DataInitializerFactory.shopReservationDateTime(shopReservation, LocalDate.now(), LocalTime.now().plusHours(1));
        var shopReservationDateTime2 = DataInitializerFactory.shopReservationDateTime(shopReservation, LocalDate.now(), LocalTime.now().plusHours(2));
        var shopReservationDateTime3 = DataInitializerFactory.shopReservationDateTime(shopReservation, LocalDate.now(), LocalTime.now().plusHours(3));
        var shopReservationDateTime4 = DataInitializerFactory.shopReservationDateTime(shopReservation, LocalDate.now(), LocalTime.now().plusHours(3));

        em.persist(shopReservationDateTime1);
        em.persist(shopReservationDateTime2);
        em.persist(shopReservationDateTime3);
        em.persist(shopReservationDateTime4);

        var shopReservationDateTimeSeat1 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime1, seat);
        var shopReservationDateTimeSeat2 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime2, seat);
        var shopReservationDateTimeSeat3 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime3, seat);
        var shopReservationDateTimeSeat4 = DataInitializerFactory.shopReservationDateTimeSeat(shopReservationDateTime4, seat);

        em.persist(shopReservationDateTimeSeat1);
        em.persist(shopReservationDateTimeSeat2);
        em.persist(shopReservationDateTimeSeat3);
        em.persist(shopReservationDateTimeSeat4);
        transaction.commit();

        transaction.begin();
        var findReservationDateTimeSeat1 = em.find(ShopReservationDateTimeSeat.class, shopReservationDateTimeSeat1.getId());
        var findReservationDateTimeSeat2 = em.find(ShopReservationDateTimeSeat.class, shopReservationDateTimeSeat2.getId());
        var findReservationDateTimeSeat3 = em.find(ShopReservationDateTimeSeat.class, shopReservationDateTimeSeat3.getId());
        var findReservationDateTimeSeat4 = em.find(ShopReservationDateTimeSeat.class, shopReservationDateTimeSeat4.getId());

        var find = em.contains(findReservationDateTimeSeat1);

        var guest = DataInitializerFactory.guest();
        em.persist(guest);

        var reservation1 = DataInitializerFactory.reservation(guest.getId(), shopReservation, 1);
        var reservation2 = DataInitializerFactory.reservation(guest.getId(), shopReservation, 1);
        var reservation3 = DataInitializerFactory.reservation(guest.getId(), shopReservation, 1);
        var reservation4 = DataInitializerFactory.reservation(guest.getId(), shopReservation, 1);

        reservation1.addShopReservationDateTimeSeats(List.of(findReservationDateTimeSeat1));
        reservation2.addShopReservationDateTimeSeats(List.of(findReservationDateTimeSeat2));
        reservation3.addShopReservationDateTimeSeats(List.of(findReservationDateTimeSeat3));
        reservation4.addShopReservationDateTimeSeats(List.of(findReservationDateTimeSeat4));

        reservation2.updateReservationStatus(ReservationStatus.NO_SHOW);
        reservation3.updateReservationStatus(ReservationStatus.CANCEL);
        reservation4.updateReservationStatus(ReservationStatus.VISITED);

        em.persist(reservation1);
        em.persist(reservation2);
        em.persist(reservation3);
        em.persist(reservation4);
        transaction.commit();

        //when
        var result = reservationRepository.findByUserIdAndReservationStatus(guest.getId(), status);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).reservationStatus()).isEqualTo(status);
    }
}