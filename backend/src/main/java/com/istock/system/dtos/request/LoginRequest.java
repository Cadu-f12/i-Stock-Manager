package com.istock.system.dtos.request;

public record LoginRequest(
        String email,
        String password
) {}