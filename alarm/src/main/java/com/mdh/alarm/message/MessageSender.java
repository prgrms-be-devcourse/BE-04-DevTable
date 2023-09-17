package com.mdh.alarm.message;

import com.mysql.cj.Messages;

public interface MessageSender {
    void send(Messages message);
}