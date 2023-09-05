package com.mdh.devtable.waiting.presentation.dto;

public record WaitingCreateRequest(
    Long userId,
    Long shopId,
    int adultCount,
    int childCount
) {
}