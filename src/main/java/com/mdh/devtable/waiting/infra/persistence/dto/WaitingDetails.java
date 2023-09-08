package com.mdh.devtable.waiting.infra.persistence.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mdh.devtable.shop.ShopType;
import com.mdh.devtable.waiting.domain.WaitingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class WaitingDetails {
    String shopName;
    ShopType shopType;
    String region;
    String phoneNumber;
    int waitingNumber;
    WaitingStatus waitingStatus;
    int adultCount;
    int childCount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    LocalDateTime updatedAt;
}
