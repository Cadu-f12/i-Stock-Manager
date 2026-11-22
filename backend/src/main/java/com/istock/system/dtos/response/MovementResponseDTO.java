package com.istock.system.dtos.response;

import java.time.LocalDateTime;

public record MovementResponseDTO(
        Long id,
        Integer quantity,
        LocalDateTime date_time,
        MovementPastilleDTO pastille,
        MovementReasonDTO reason,
        MovementUserDTO user
) {}
