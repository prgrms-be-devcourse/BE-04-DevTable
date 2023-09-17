package com.mdh.alarm.slack;

import com.mdh.alarm.message.MessageSender;
import com.mdh.alarm.slack.SlackAlertRequest;
import com.mdh.alarm.slack.SlackClient;
import com.mysql.cj.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SlackMessageSender implements MessageSender {

    private final SlackClient slackClient;

    @Override
    public void send(Messages message) {
        slackClient.requestAlert(new SlackAlertRequest(message.toString()));
    }
}