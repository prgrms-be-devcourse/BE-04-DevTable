package com.mdh.common.waiting.persistence;

import com.mdh.common.DataInitializerFactory;
import com.mdh.common.global.config.JpaConfig;
import com.mdh.common.shop.persistence.RegionRepository;
import com.mdh.common.shop.persistence.ShopRepository;
import com.mdh.common.user.persistence.UserRepository;
import com.mdh.common.waiting.domain.ShopWaitingStatus;
import com.mdh.common.waiting.domain.WaitingStatus;
import com.mdh.common.waiting.persistence.dto.WaitingAlarmInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@DataJpaTest
@Import({JpaConfig.class})
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WaitingRepositoryTest {

    @Autowired
    private WaitingRepository waitingRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShopWaitingRepository shopWaitingRepository;

    @Autowired
    private ShopRepository shopRepository;

    @ParameterizedTest
    @ValueSource(strings = {"PROGRESS", "CANCEL", "NO_SHOW", "VISITED"})
    @DisplayName("유저의 웨이팅 목록을 상태별로 조회 할 수 있다.")
    void findAllByUserIdAndWaitingStatus(String waitingStatus) {
        //given
        var owner = DataInitializerFactory.owner();
        userRepository.save(owner);

        //매장 정보 생성
        var region = DataInitializerFactory.region();
        regionRepository.save(region);

        var shopAddress = DataInitializerFactory.shopAddress();
        var shopDetails = DataInitializerFactory.shopDetails();

        var shop = DataInitializerFactory.shop(owner.getId(), shopDetails, region, shopAddress);
        shopRepository.save(shop);

        var shopWaiting = DataInitializerFactory.shopWaiting(shop.getId(), 30, 8, 2);
        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);
        shopWaitingRepository.save(shopWaiting);

        //웨이팅 등록
        var guest = DataInitializerFactory.guest();
        userRepository.save(guest);

        var waitingPeople = DataInitializerFactory.waitingPeople(2, 3);
        var waiting = DataInitializerFactory.waiting(guest.getId(), shopWaiting, waitingPeople);

        waiting.changeWaitingStatus(WaitingStatus.valueOf(waitingStatus));
        waitingRepository.save(waiting);

        //when
        var findUserWaitings = waitingRepository.findAllByUserIdAndWaitingStatus(guest.getId(), WaitingStatus.valueOf(waitingStatus));

        //then
        assertThat(findUserWaitings).hasSize(1);
        assertThat(findUserWaitings).extracting("shopId", "waitingId")
                .contains(tuple(shop.getId(), waiting.getId()));
    }

    @Test
    @DisplayName("웨이팅 아이디로 웨이팅 상세 정보를 가져온다.")
    void findWaitingDetails() {
        //given
        var guest = DataInitializerFactory.guest();
        var savedUser = userRepository.save(guest);

        var region = DataInitializerFactory.region();
        regionRepository.save(region);

        var shopDetails = DataInitializerFactory.shopDetails();
        var shopAddress = DataInitializerFactory.shopAddress();
        var shop = DataInitializerFactory.shop(guest.getId(), shopDetails, region, shopAddress);
        var savedShop = shopRepository.save(shop);

        var shopWaiting = DataInitializerFactory.shopWaiting(savedShop.getId(), 20, 7, 2);
        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);
        var savedShopWaiting = shopWaitingRepository.save(shopWaiting);

        var waitingPeople = DataInitializerFactory.waitingPeople(2, 0);
        var waiting = DataInitializerFactory.waiting(savedUser.getId(), savedShopWaiting, waitingPeople);
        var savedWaiting = waitingRepository.save(waiting);

        //when
        var waitingDetails = waitingRepository.findByWaitingDetails(savedWaiting.getId()).orElse(null);

        //then
        assertThat(waitingDetails.shopId()).isEqualTo(shop.getId());
        assertThat(waitingDetails.shopName()).isEqualTo(shop.getName());
        assertThat(waitingDetails.region()).isEqualTo(region.getDistrict());
        assertThat(waitingDetails.shopType()).isEqualTo(shop.getShopType());
        assertThat(waitingDetails.shopDetails()).usingRecursiveComparison().isEqualTo(shopDetails);
        assertThat(waitingDetails.waitingNumber()).isEqualTo(waiting.getWaitingNumber());
        assertThat(waitingDetails.waitingStatus()).isEqualTo(waiting.getWaitingStatus());
        assertThat(waitingDetails.waitingPeople()).usingRecursiveComparison().isEqualTo(waitingPeople);
        assertThat(waitingDetails.createdDate()).isCloseTo(waiting.getCreatedDate(), within(1, ChronoUnit.SECONDS));
        assertThat(waitingDetails.modifiedDate()).isCloseTo(waiting.getModifiedDate(), within(1, ChronoUnit.SECONDS));
    }

    @Test
    @DisplayName("웨이팅 아이디로 알람 정보를 가져온다.")
    void findWaitingAlarmInfoByIdTest() {
        // Given
        var owner = DataInitializerFactory.owner();
        userRepository.save(owner);

        var region = DataInitializerFactory.region();
        regionRepository.save(region);

        var shopAddress = DataInitializerFactory.shopAddress();
        var shopDetails = DataInitializerFactory.shopDetails();

        var shop = DataInitializerFactory.shop(owner.getId(), shopDetails, region, shopAddress);
        shopRepository.save(shop);

        var shopWaiting = DataInitializerFactory.shopWaiting(shop.getId(), 30, 8, 2);
        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);
        shopWaitingRepository.save(shopWaiting);

        var guest = DataInitializerFactory.guest();
        userRepository.save(guest);

        var waitingPeople = DataInitializerFactory.waitingPeople(2, 3);
        var waiting = DataInitializerFactory.waiting(guest.getId(), shopWaiting, waitingPeople);
        waitingRepository.save(waiting);

        Long waitingId = waiting.getId(); // 실제로 저장된 Waiting 엔터티의 ID

        // When
        var optionalAlarmInfo = waitingRepository.findWaitingAlarmInfoById(waitingId);

        // Then
        assertThat(optionalAlarmInfo).isPresent();
        var alarmInfo = optionalAlarmInfo.get();

        assertThat(alarmInfo)
                .extracting(WaitingAlarmInfo::userId, WaitingAlarmInfo::shopName, WaitingAlarmInfo::shopPhoneNumber)
                .containsExactly(
                        guest.getId(),
                        shop.getName(),
                        shopDetails.getPhoneNumber()
                );
    }

}