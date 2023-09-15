package com.mdh.user.global;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ApiResponse<T>(
        int statusCode,
        T data,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime serverDateTime
) {

    public static <T> ApiResponse<T> fail(int statusCode, T data) {
        return new ApiResponse<>(statusCode, data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(201, data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> noContent(T data) {
        return new ApiResponse<>(204, data, LocalDateTime.now());
    }
}