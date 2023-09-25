package com.mdh.owner.waiting.infra.eventbus.waitingpoll;

import com.mdh.common.waiting.domain.ShopWaitingStatus;
import com.mdh.common.waiting.domain.Waiting;
import com.mdh.common.waiting.persistence.WaitingLine;
import com.mdh.owner.DataInitializerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WaitingLinePollerTest {

    @InjectMocks
    private WaitingLinePoller waitingLinePoller;

    @Mock
    private WaitingLine waitingLine;

    @Mock
    private StringRedisTemplate redisTemplate;

    @DisplayName("웨이팅 순번이 1인 경우 손님에게 알람이 발생한다.")
    @Test
    void shouldSendAlarmWhenRankIsFirst() {
        // Given
        var shopId = 1L;
        var userId = 1L;
        var rank = 1L;
        var shopWaiting = DataInitializerFactory.shopWaiting(shopId, 3, 4, 1);
        shopWaiting.changeShopWaitingStatus(ShopWaitingStatus.OPEN);
        var waitingPeople = DataInitializerFactory.waitingPeople(2, 1);
        var waiting = DataInitializerFactory.waiting(userId, shopWaiting, waitingPeople);

        when(waitingLine.visit(shopId)).thenReturn(Optional.of(userId));
        when(waitingLine.findRank(shopId, waiting.getId(), waiting.getIssuedTime())).thenReturn(Optional.of(rank));
        when(redisTemplate.convertAndSend(any(), any())).thenReturn(1L);

        // When
        waitingLinePoller.pollWaitingLine(waiting);

        // Then
        verify(redisTemplate, times(1)).convertAndSend(any(), any());
    }
}