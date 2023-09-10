package com.mdh.devtable.waiting.infra.persistence;

import com.mdh.devtable.DataInitializerFactory;
import com.mdh.devtable.global.config.JpaConfig;
import com.mdh.devtable.shop.infra.persistence.RegionRepository;
import com.mdh.devtable.shop.infra.persistence.ShopRepository;
import com.mdh.devtable.user.infra.persistence.UserRepository;
import com.mdh.devtable.waiting.domain.ShopWaitingStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DataJpaTest
@Import({JpaConfig.class})
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WaitingRepositoryTest {

    @Autowired
    private WaitingRepository waitingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private ShopWaitingRepository shopWaitingRepository;

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
}