package com.mdh.alarm.message;

public record AlarmMessage(
        String to,
        String from,
        String message
) {
}