package com.core.book.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final int status;
    private final boolean success;
    private final String message;
    private T data;

    public static <T> ApiResponse<T> success(SuccessStatus status, T data) {
        return ApiResponse.<T>builder()
                .status(status.getStatusCode())
                .success(true)
                .message(status.getMessage())
                .data(data)
                .build();
    }

    public static ApiResponse<Void> success_only(SuccessStatus status) {
        return ApiResponse.<Void>builder()
                .status(status.getStatusCode())
                .success(true)
                .message(status.getMessage())
                .build();
    }

    public static ApiResponse<Void> fail(int status, String message) {
        return ApiResponse.<Void>builder()
                .status(status)
                .success(false)
                .message(message)
                .build();
    }
}
