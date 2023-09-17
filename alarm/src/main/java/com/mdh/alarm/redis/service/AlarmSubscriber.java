
package com.mdh.alarm.redis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdh.alarm.message.AlarmMessage;
import com.mdh.alarm.message.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.connection.MessageListener;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmSubscriber implements MessageListener {

    private final MessageSender messageSender;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            var received = objectMapper.readValue(message.getBody(), AlarmMessage.class);
            messageSender.send(received);
        } catch (IOException e) {
            log.error("Json 파싱 실패 {}", e.getMessage());
            throw new RuntimeException(e);
        }

    }
}