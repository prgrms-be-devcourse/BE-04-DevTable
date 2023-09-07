package com.mdh.devtable.waiting.application;

import com.mdh.devtable.shop.*;
import com.mdh.devtable.shop.infra.persistence.RegionRepository;
import com.mdh.devtable.shop.infra.persistence.ShopRepository;
import com.mdh.devtable.user.domain.Role;
import com.mdh.devtable.user.domain.User;
import com.mdh.devtable.user.infra.persistence.UserRepository;
import com.mdh.devtable.waiting.domain.ShopWaiting;
import com.mdh.devtable.waiting.domain.ShopWaitingStatus;
import com.mdh.devtable.waiting.domain.WaitingStatus;
import com.mdh.devtable.waiting.infra.persistence.ShopWaitingRepository;
import com.mdh.devtable.waiting.infra.persistence.WaitingRepository;
import com.mdh.devtable.waiting.presentation.dto.WaitingCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class WaitingServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ShopWaitingRepository shopWaitingRepository;

    @Autowired
    private WaitingRepository waitingRepository;

    @Autowired
    private WaitingService waitingService;

    @Test
    @DisplayName("웨이팅을 생성한다.")
    void createWaitingTest() {
        //given
        var ownerId = initUser(Role.OWNER, "owner@example.com");

        var regionId = initRegion();
        var region = regionRepository.findById(regionId)
                .orElse(null);

        var shopId = initShop(ownerId, region);

        var shopWaiting = ShopWaiting.builder()
                .shopId(shopId)
                .maximumWaiting(20)
                .maximumWaitingPeople(7)
                .minimumWaitingPeople(2)
                .build();

        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);
        shopWaitingRepository.save(shopWaiting);

        var userId = initUser(Role.GUEST, "guest@example.com");
        var waitingCreateRequest = new WaitingCreateRequest(userId, shopId, 2, 0);

        //when
        var waitingId = waitingService.createWaiting(waitingCreateRequest);
        var findShopWaiting = shopWaitingRepository.findById(shopWaiting.getShopId())
                .orElse(null);
        var findWaiting = waitingRepository.findById(waitingId)
                .orElse(null);

        //then
        assertThat(findWaiting.getWaitingNumber()).isEqualTo(findShopWaiting.getWaitingCount());
        assertThat(findWaiting.getWaitingNumber()).isEqualTo(1);
        assertThat(findWaiting).isNotNull();
    }

    @Test
    @DisplayName("웨이팅을 취소한다.")
    void cancelWaitingTest() {
        //given
        var ownerId = initUser(Role.OWNER, "owner@example.com");

        var regionId = initRegion();
        var region = regionRepository.findById(regionId)
                .orElse(null);

        var shopId = initShop(ownerId, region);

        var shopWaiting = ShopWaiting.builder()
                .shopId(shopId)
                .maximumWaiting(20)
                .maximumWaitingPeople(7)
                .minimumWaitingPeople(2)
                .build();

        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);
        shopWaitingRepository.save(shopWaiting);

        var userId = initUser(Role.GUEST, "guest@example.com");
        var waitingCreateRequest = new WaitingCreateRequest(userId, shopId, 2, 0);
        var waitingId = waitingService.createWaiting(waitingCreateRequest);

        //when
        waitingService.cancelWaiting(waitingId);

        //then
        var findWaiting = waitingRepository.findById(waitingId)
                .orElse(null);
        assertThat(findWaiting.getWaitingStatus()).isEqualTo(WaitingStatus.CANCEL);
    }

    @Test
    @DisplayName("웨이팅이 등록된 상태에서 웨이팅을 추가로 등록 할 수 없다.")
    void createWaitingWithProgressingWaitingTest() {
        //given
        var ownerId = initUser(Role.OWNER, "owner@example.com");

        var regionId = initRegion();
        var region = regionRepository.findById(regionId)
                .orElse(null);

        var shopId = initShop(ownerId, region);

        var shopWaiting = ShopWaiting.builder()
                .shopId(shopId)
                .maximumWaiting(20)
                .maximumWaitingPeople(7)
                .minimumWaitingPeople(2)
                .build();

        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);
        shopWaitingRepository.save(shopWaiting);

        var userId = initUser(Role.GUEST, "guest@example.com");

        var waitingCreateRequest1 = new WaitingCreateRequest(userId, shopId, 2, 0);
        waitingService.createWaiting(waitingCreateRequest1);

        var waitingCreateRequest2 = new WaitingCreateRequest(userId, shopId, 2, 0);

        //when & then
        assertThatThrownBy(() -> waitingService.createWaiting(waitingCreateRequest2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("해당 매장에 이미 웨이팅이 등록되어있다면 웨이팅을 추가로 등록 할 수 없다. userId : " + userId);
    }

    private Long initUser(Role role, String email) {
        var user = User.builder()
                .email(email)
                .role(role)
                .password("password123")
                .build();

        userRepository.save(user);
        return user.getId();
    }

    private Long initRegion() {
        var region = Region.builder()
                .city("서울시")
                .district("강남구")
                .build();

        regionRepository.save(region);
        return region.getId();
    }

    private Long initShop(Long userId, Region region) {
        var shop = Shop.builder()
                .userId(userId)
                .name("가게 이름")
                .description("가게의 간단한 설명")
                .shopType(ShopType.AMERICAN)
                .shopDetails(ShopDetails.builder()
                        .url("https://www.example.com")
                        .phoneNumber("123-456-7890")
                        .openingHours("월-토 : 10:00 AM - 9:00 PM")
                        .holiday("일요일 휴무")
                        .introduce("이 가게는 맛있는 음식을 제공합니다.")
                        .info("추가 정보 없음")
                        .build())
                .region(region)
                .shopAddress(ShopAddress.builder()
                        .address("예시로 123번지")
                        .zipcode("12345")
                        .latitude("37.123456")
                        .longitude("128.124233")
                        .build())
                .build();

        shopRepository.save(shop);
        return shop.getId();
    }
}