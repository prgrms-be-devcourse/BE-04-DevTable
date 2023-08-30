package com.mdh.devtable.global;

public record ApiResponse<T>(
        int statusCode,
        T data
) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, data);
    }
}
