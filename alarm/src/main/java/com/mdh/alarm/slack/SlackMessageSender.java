package com.mdh.alarm.slack;

import com.mdh.alarm.message.AlarmMessage;
import com.mdh.alarm.message.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SlackMessageSender implements MessageSender {

    private final SlackClient slackClient;

    @Override
    public void send(AlarmMessage message) {
        slackClient.requestAlert(new SlackAlertRequest(message.toString()));
    }
}