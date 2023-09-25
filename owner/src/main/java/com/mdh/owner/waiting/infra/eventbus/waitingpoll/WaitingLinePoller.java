package com.mdh.owner.waiting.infra.eventbus.waitingpoll;

import com.mdh.common.waiting.domain.Waiting;
import com.mdh.common.waiting.persistence.WaitingLine;
import com.mdh.owner.global.message.AlarmMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class WaitingLinePoller {

    private final WaitingLine waitingLine;

    @Value("${spring.data.redis.topic.alarm}")
    private String topic;

    private final StringRedisTemplate redisTemplate;

    public void pollWaitingLine(Waiting waiting) {

        var shopId = waiting.getShopWaiting().getShopId();

        waitingLine.visit(shopId).ifPresent(userId -> {
            waitingLine.findRank(shopId, waiting.getId(), waiting.getIssuedTime())
                    .filter(rank -> rank == 1)
                    .ifPresent(rank -> {
                        var to = "웨이팅 1순위 손님 id:" + userId;
                        var from = "매장 id: " + shopId;
                        var message = "웨이팅 순번이 1입니다.";
                        redisTemplate.convertAndSend(topic, new AlarmMessage(to, from, message));
                    });
        });
    }
}