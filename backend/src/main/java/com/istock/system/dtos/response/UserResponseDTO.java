package com.istock.system.dtos.response;

import com.istock.system.entities.Movement;

import java.util.List;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        Boolean isActive,
        List<Movement> movement
) {}
