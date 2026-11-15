package com.istock.system.dtos.response;

import com.istock.system.entities.MovementType;

public record MovementReasonDTO(
        Long id,
        String description,
        MovementType type
) {}
