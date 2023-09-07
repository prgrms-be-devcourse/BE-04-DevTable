package com.mdh.devtable;

import com.mdh.devtable.shop.*;
import com.mdh.devtable.user.domain.Role;
import com.mdh.devtable.user.domain.User;
import com.mdh.devtable.waiting.domain.ShopWaiting;
import com.mdh.devtable.waiting.domain.Waiting;
import com.mdh.devtable.waiting.domain.WaitingPeople;

public final class DataInitializerFactory {

    public static User owner() {
        return User.builder()
                .email("owner@example.com")
                .role(Role.OWNER)
                .password("password123")
                .build();
    }

    public static User guest() {
        return User.builder()
                .email("guest@example.com")
                .role(Role.GUEST)
                .password("password123")
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
}