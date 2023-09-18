package com.mdh.common.waiting.persistence.dto;

public record WaitingAlarmInfo(
        String shopInfo,
        String shopName,
        int totalPeople,
        int waitingNumber,
        String shopPhoneNumber,
        Long userId
) {
}