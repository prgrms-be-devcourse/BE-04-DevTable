package com.mdh.owner.global.message;

public record AlarmMessage(
        String to,
        String from,
        String message
) {
}