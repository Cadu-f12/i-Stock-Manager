package com.istock.system.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record MovementDTO(
        Long id,
        PastilleDTO pastille,
        UserDTO user,
        @JsonProperty("reason_movement")
        ReasonMovementDTO reasonMovement,
        Integer quantity,
        LocalDateTime dateTime
) {}
