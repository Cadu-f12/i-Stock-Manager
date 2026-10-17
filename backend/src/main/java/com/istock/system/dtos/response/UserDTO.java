package com.istock.system.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record UserDTO(
        Long id,
        String name,
        String email,
        @JsonProperty("encrypted_password")
        String encryptedPassword,
        @JsonProperty("is_active")
        Boolean isActive,
        List<MovementDTO> movements
) {}
