package com.mdh.alarm.redis.service;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdh.alarm.message.AlarmMessage;
import com.mdh.alarm.message.MessageSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlarmSubscriberTest {

    @Mock
    private MessageSender messageSender;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AlarmSubscriber alarmSubscriber;

    @Test
    void testOnMessage() throws Exception {
        // given
        var message = mock(Message.class);
        var pattern = new byte[0];
        var receivedMessage = new AlarmMessage("John", "Dev-table", "test");
        var mockMessageBody = "mockMessageBody".getBytes();

        when(message.getBody()).thenReturn(mockMessageBody);
        when(objectMapper.readValue(any(byte[].class), eq(AlarmMessage.class))).thenReturn(receivedMessage);

        // when
        alarmSubscriber.onMessage(message, pattern);

        // then
        verify(messageSender, times(1)).send(receivedMessage);
    }

    @Test
    void testOnMessage_ThrowsException() throws Exception {
        // given
        var message = mock(Message.class);
        var pattern = new byte[0];

        when(objectMapper.readValue(any(byte[].class), eq(AlarmMessage.class))).thenThrow(new IOException());

        // when & then
        assertThrows(RuntimeException.class, () -> {
            alarmSubscriber.onMessage(message, pattern);
        });
    }
}