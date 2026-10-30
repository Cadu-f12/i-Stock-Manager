package com.istock.system.dtos.response;

public record LoginResponse(
        String accessToken,
        String tokenType
) {}
