package com.istock.system.dtos.response;

public record FieldError(
        String field,
        String message
) {}
