package com.mdh.devtable.ownerwaiting.infra.persistence;

import com.mdh.devtable.DataInitializerFactory;
import com.mdh.devtable.global.config.JpaConfig;
import com.mdh.devtable.shop.infra.persistence.RegionRepository;
import com.mdh.devtable.shop.infra.persistence.ShopRepository;
import com.mdh.devtable.user.infra.persistence.UserRepository;
import com.mdh.devtable.waiting.domain.ShopWaitingStatus;
import com.mdh.devtable.waiting.domain.WaitingStatus;
import com.mdh.devtable.waiting.infra.persistence.ShopWaitingRepository;
import com.mdh.devtable.waiting.infra.persistence.WaitingRepository;
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
class OwnerWaitingRepositoryTest {

    @Autowired
    private WaitingRepository waitingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShopWaitingRepository shopWaitingRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private RegionRepository regionRepository;

    @DisplayName("점주 id, 웨이팅 상태로 웨이팅을 조회할 수 있다.")
    @Test
    void findWaitingByOwnerIdAndWaitingStatus() {
        //given
        var owner = DataInitializerFactory.owner();
        userRepository.save(owner);

        var region = DataInitializerFactory.region();
        regionRepository.save(region);

        var shopDetails = DataInitializerFactory.shopDetails();
        var shopAddress = DataInitializerFactory.shopAddress();
        var shop = DataInitializerFactory.shop(owner.getId(), shopDetails, region, shopAddress);
        shopRepository.save(shop);

        var shopWaiting = DataInitializerFactory.shopWaiting(shop.getId(), 30, 8, 2);
        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);
        shopWaiting.updateChildEnabled(true);
        shopWaitingRepository.save(shopWaiting);

        var waitingPeople = DataInitializerFactory.waitingPeople(2, 3);
        var waiting = DataInitializerFactory.waiting(owner.getId(), shopWaiting, waitingPeople);
        waiting.changeWaitingStatus(WaitingStatus.PROGRESS);
        waitingRepository.save(waiting);

        //when
        var result = waitingRepository.findWaitingByOwnerIdAndWaitingStatus(owner.getId(), WaitingStatus.PROGRESS);

        //then
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).phoneNumber()).isEqualTo(owner.getPhoneNumber());
        assertThat(result.get(0).waitingNumber()).isEqualTo(waiting.getWaitingNumber());
    }
}