package com.istock.system.dtos.request;

import com.istock.system.entities.MovementType;

public record RegisterMovementRequest(
    Long pastille,
    String description,
    MovementType type,
    Integer quantity
) {}
