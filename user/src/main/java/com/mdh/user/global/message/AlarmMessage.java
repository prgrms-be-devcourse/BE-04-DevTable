package com.mdh.user.global.message;

public record AlarmMessage(
        String to,
        String from,
        String message
) {
}