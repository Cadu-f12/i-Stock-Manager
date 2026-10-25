package com.istock.system.dtos.response;

public record ApiError(
        int status,
        String error,
        String message
) {}
