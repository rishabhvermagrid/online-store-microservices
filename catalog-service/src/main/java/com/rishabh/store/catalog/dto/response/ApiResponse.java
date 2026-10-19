package com.rishabh.store.catalog.dto.response;

import java.time.Instant;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        Instant timestamp
) {
    public ApiResponse(boolean success, String message, T data) {
        this(success, message, data, Instant.now());
    }
}
