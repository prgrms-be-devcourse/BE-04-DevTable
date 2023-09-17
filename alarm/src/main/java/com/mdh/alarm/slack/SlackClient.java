package com.mdh.alarm.slack;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "${slack-client.name}",
        url =  "${slack-client.domain-url}",
        fallbackFactory = SlackClient.SlackFallback.class
)
public interface SlackClient {

    @PostMapping
    String requestAlert(@RequestBody SlackAlertRequest request);

    @Slf4j
    @Component
    class SlackFallback implements FallbackFactory<SlackClient> {

        @Override
        public SlackClient create(Throwable cause) {
            log.warn("슬랙 api 응답 오류: {}", cause.getMessage());
            return (request) -> "load failed";
        }
    }
}