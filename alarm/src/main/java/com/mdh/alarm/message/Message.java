package com.mdh.alarm.message;

public record Message(
        String to,
        String from,
        String message
) {
}