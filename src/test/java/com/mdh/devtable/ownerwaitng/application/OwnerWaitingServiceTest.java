package com.mdh.devtable.ownerwaitng.application;

import com.mdh.devtable.ownerwaitng.presentaion.OwnerWaitingChangeRequest;
import com.mdh.devtable.waiting.domain.ShopWaiting;
import com.mdh.devtable.waiting.domain.ShopWaitingStatus;
import com.mdh.devtable.waiting.infra.persistence.ShopWaitingRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


@ActiveProfiles("test")
@SpringBootTest
@Transactional
class OwnerWaitingServiceTest {

    @Autowired
    private OwnerWaitingService ownerWaitingService;

    @Autowired
    private ShopWaitingRepository shopWaitingRepository;

    @DisplayName("매장 웨이팅 상태를 변경할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"OPEN", "BREAK_TIME"})
    void changShopWaitingStatus(String status) {
        //given
        var shopWaiting = ShopWaiting
                .builder()
                .shopId(1L)
                .maximumWaitingPeople(2)
                .minimumWaitingPeople(1)
                .maximumWaiting(10)
                .build();
        shopWaitingRepository.save(shopWaiting);

        //when
        var request = new OwnerWaitingChangeRequest(status);
        ownerWaitingService.changeShopWaitingStatus(shopWaiting.getShopId(), request);

        //then
        var updatedShopWaiting = shopWaitingRepository.findById(shopWaiting.getShopId()).orElseThrow();
        Assertions.assertThat(ShopWaitingStatus.valueOf(status)).isEqualTo(updatedShopWaiting.getShopWaitingStatus());
    }
}