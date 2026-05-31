package com.petcare.care.dto;

public record ApiResponse<T>(boolean success, String message, T data) {
    public static <T> ApiResponse<T> success(String msg, T data) {
        return new ApiResponse<>(true, msg, data);
    }
    public static <T> ApiResponse<T> failure(String msg) {
        return new ApiResponse<>(false, msg, null);
    }
}
