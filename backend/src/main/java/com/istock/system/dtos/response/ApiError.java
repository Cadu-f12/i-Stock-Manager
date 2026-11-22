package com.istock.system.dtos.response;

import java.util.List;

public record ApiError(
        int status,
        String error,
        String message,
        List<FieldError> fields
) {
    public ApiError(int status, String error, String message) {
        this(status, error, message, List.of());
    }
}
