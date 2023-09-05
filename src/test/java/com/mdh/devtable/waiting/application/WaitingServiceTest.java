package com.mdh.devtable.waiting.application;

import com.mdh.devtable.waiting.ShopWaiting;
import com.mdh.devtable.waiting.ShopWaitingStatus;
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

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class WaitingServiceTest {

    @Autowired
    private WaitingRepository waitingRepository;

    @Autowired
    private ShopWaitingRepository shopWaitingRepository;

    @Autowired
    private WaitingService waitingService;

    @Test
    @DisplayName("웨이팅을 생성한다.")
    void createWaitingTest() {
        //given
        var shopId = 1L;
        var userId = 1L;

        var shopWaiting = ShopWaiting.builder()
            .shopId(1L)
            .maximumWaiting(20)
            .maximumWaitingPeople(7)
            .minimumWaitingPeople(2)
            .build();
        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);
        shopWaitingRepository.save(shopWaiting);

        var waitingCreateRequest = new WaitingCreateRequest(userId, shopId, 2, 0);

        //when
        var waitingId = waitingService.createWaiting(waitingCreateRequest);

        //then
        var findWaiting = waitingRepository.findById(waitingId).orElse(null);
        assertThat(findWaiting).isNotNull();
    }
}