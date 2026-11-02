package com.istock.system.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record MovementDTO(
        Long id,
        Integer quantity,
        @JsonProperty("date_time")
        LocalDateTime dateTime,
        ReasonMovementDTO reason,
        UserDTO user
) {}
