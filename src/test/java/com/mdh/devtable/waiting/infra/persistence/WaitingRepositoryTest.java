package com.mdh.devtable.waiting.infra.persistence;

import com.mdh.devtable.DataInitializerFactory;
import com.mdh.devtable.global.config.JpaConfig;
import com.mdh.devtable.shop.infra.persistence.RegionRepository;
import com.mdh.devtable.shop.infra.persistence.ShopRepository;
import com.mdh.devtable.user.infra.persistence.UserRepository;
import com.mdh.devtable.waiting.domain.ShopWaitingStatus;
import com.mdh.devtable.waiting.domain.WaitingStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
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
}