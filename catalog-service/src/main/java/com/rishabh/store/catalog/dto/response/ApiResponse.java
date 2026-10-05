package com.rishabh.store.catalog.dto.response;

public record ApiResponse<T>(
    boolean success,
    String message,
    T data
){}
