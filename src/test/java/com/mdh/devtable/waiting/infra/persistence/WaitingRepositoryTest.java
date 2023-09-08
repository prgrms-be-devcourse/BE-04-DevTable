package com.mdh.devtable.waiting.infra.persistence;

import com.mdh.devtable.DataInitializerFactory;
import com.mdh.devtable.shop.infra.persistence.RegionRepository;
import com.mdh.devtable.shop.infra.persistence.ShopRepository;
import com.mdh.devtable.user.infra.persistence.UserRepository;
import com.mdh.devtable.waiting.domain.ShopWaitingStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
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

        var shopWaiting = DataInitializerFactory.shopWaiting(savedUser.getId(), 20, 7, 2);
        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);
        var savedShopWaiting = shopWaitingRepository.save(shopWaiting);

        var waitingPeople = DataInitializerFactory.waitingPeople(2, 0);
        var waiting = DataInitializerFactory.waiting(savedUser.getId(), shopWaiting, waitingPeople);
        var savedWaiting = waitingRepository.save(waiting);

        //when
        var waitingDetails = waitingRepository.findByWaitingDetails(savedWaiting.getId()).orElse(null);

        //then
        assertThat(waitingDetails.shopName()).isEqualTo(shop.getName());
        assertThat(waitingDetails.region()).isEqualTo(region.getDistrict());
        assertThat(waitingDetails.shopType()).isEqualTo(shop.getShopType());
        assertThat(waitingDetails.shopDetails()).isEqualTo(shopDetails);
        assertThat(waitingDetails.waitingNumber()).isEqualTo(waiting.getWaitingNumber());
        assertThat(waitingDetails.waitingStatus()).isEqualTo(waiting.getWaitingStatus());
        assertThat(waitingDetails.waitingPeople()).isEqualTo(waitingPeople);
        assertThat(waitingDetails.createdDate()).isEqualTo(waiting.getCreatedDate());
        assertThat(waitingDetails.modifiedDate()).isEqualTo(waiting.getModifiedDate());
    }
}