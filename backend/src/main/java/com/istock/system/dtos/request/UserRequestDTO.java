package com.istock.system.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.istock.system.dtos.response.MovementDTO;

import java.util.List;

public record UserRequestDTO(
        String role,
        String name,
        String email,
        @JsonProperty("password")
        String password,
        @JsonProperty("isActive")
        Boolean isActive
) {}
