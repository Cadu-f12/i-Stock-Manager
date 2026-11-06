package com.istock.system.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.istock.system.dtos.response.MovementDTO;

import java.util.List;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        Boolean isActive
) {}
