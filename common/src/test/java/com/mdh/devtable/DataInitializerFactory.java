package com.mdh.devtable;

import com.mdh.devtable.menu.domain.MealType;
import com.mdh.devtable.menu.domain.Menu;
import com.mdh.devtable.menu.domain.MenuCategory;
import com.mdh.devtable.menu.domain.MenuType;
import com.mdh.devtable.reservation.*;
import com.mdh.devtable.shop.domain.*;
import com.mdh.devtable.user.domain.Role;
import com.mdh.devtable.user.domain.User;
import com.mdh.devtable.waiting.domain.ShopWaiting;
import com.mdh.devtable.waiting.domain.Waiting;
import com.mdh.devtable.waiting.domain.WaitingPeople;
import com.mdh.devtable.waiting.domain.WaitingStatus;
import com.mdh.devtable.waiting.persistence.dto.WaitingDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class DataInitializerFactory {

    public static User owner() {
        return User.builder()
                .email("owner@example.com")
                .role(Role.OWNER)
                .password("password123")
                .phoneNumber("01056781234")
                .build();
    }

    public static User guest() {
        return User.builder()
                .email("guest@example.com")
                .role(Role.GUEST)
                .password("password123")
                .phoneNumber("01012345678")
                .build();
    }

    public static Region region() {
        return Region.builder()
                .city("서울시")
                .district("강남구")
                .build();
    }

    public static ShopDetails shopDetails() {
        return ShopDetails.builder()
                .url("https://www.example.com")
                .phoneNumber("123-456-7890")
                .openingHours("월-토 : 10:00 AM - 9:00 PM")
                .holiday("일요일 휴무")
                .introduce("이 가게는 맛있는 음식을 제공합니다.")
                .info("추가 정보 없음")
                .build();
    }

    public static ShopAddress shopAddress() {
        return ShopAddress.builder()
                .address("예시로 123번지")
                .zipcode("12345")
                .latitude("37.123456")
                .longitude("128.124233")
                .build();
    }

    public static Shop shop(
            Long userId,
            ShopDetails shopDetails,
            Region region,
            ShopAddress shopAddress
    ) {
        return Shop.builder()
                .userId(userId)
                .name("가게 이름")
                .description("가게의 간단한 설명")
                .shopType(ShopType.AMERICAN)
                .shopDetails(shopDetails)
                .region(region)
                .shopAddress(shopAddress)
                .build();
    }

    public static ShopWaiting shopWaiting(
            Long shopId,
            int maximumWaiting,
            int maximumWaitingPeople,
            int minimumWaitingPeople) {
        return ShopWaiting.builder()
                .shopId(shopId)
                .maximumWaiting(maximumWaiting)
                .maximumWaitingPeople(maximumWaitingPeople)
                .minimumWaitingPeople(minimumWaitingPeople)
                .build();
    }

    public static WaitingPeople waitingPeople(int adultCount, int childCount) {
        return new WaitingPeople(adultCount, childCount);
    }

    public static Waiting waiting(Long userId, ShopWaiting shopWaiting, WaitingPeople waitingPeople) {
        return Waiting.builder()
                .userId(userId)
                .shopWaiting(shopWaiting)
                .waitingPeople(waitingPeople)
                .build();
    }

    public static WaitingDetails waitingDetails(ShopDetails shopDetails,
                                                WaitingStatus waitingStatus,
                                                WaitingPeople waitingPeople) {
        return new WaitingDetails(1L,
                "가게 이름",
                ShopType.KOREAN,
                "강남구",
                shopDetails,
                85,
                waitingStatus,
                waitingPeople,
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    //== 예약 도메인 ==//
    public static ShopReservation shopReservation(Long shopId, int minimumCount, int maximumCount) {
        return new ShopReservation(shopId, minimumCount, maximumCount);
    }

    public static Reservation reservation(Long userId, ShopReservation shopReservation, int personCount) {
        return Reservation.builder()
                .userId(userId)
                .shopReservation(shopReservation)
                .requirement("요구사항 입니다. 요구 사항은 null 일 수 있습니다.")
                .personCount(personCount)
                .build();
    }

    public static Reservation preemptiveReservation(Long userId, int personCount) {
        return new Reservation(userId, "요구사항 입니다. 요구 사항은 null 일 수 있습니다.", personCount);
    }

    public static Seat seat(ShopReservation shopReservation, int seatCount) {
        return new Seat(shopReservation, seatCount, SeatType.ROOM);
    }

    public static ShopReservationDateTime shopReservationDateTime(
            ShopReservation shopReservation) {
        return new ShopReservationDateTime(shopReservation, LocalDate.now(), LocalTime.now());
    }

    public static ShopReservationDateTime shopReservationDateTime(
            ShopReservation shopReservation,
            LocalDate localDate,
            LocalTime localTime) {
        return new ShopReservationDateTime(shopReservation, localDate, localTime);
    }

    public static ShopReservationDateTimeSeat shopReservationDateTimeSeat(ShopReservationDateTime shopReservationDateTime,
                                                                          Seat seat) {
        return new ShopReservationDateTimeSeat(shopReservationDateTime, seat);
    }

    public static MenuCategory menuCategory(Long shopId) {
        return new MenuCategory(shopId, "Main Course", "Delicious main courses");
    }

    public static Menu menu() {
        return Menu.builder()
                .mealType(MealType.BREAKFAST)
                .menuType(MenuType.APPETIZER)
                .price(1)
                .label("label")
                .menuName("menu")
                .description("description")
                .build();
    }

    public static Seat seat(ShopReservation shopReservation) {
        return new Seat(shopReservation, 3, SeatType.BAR);
    }
}