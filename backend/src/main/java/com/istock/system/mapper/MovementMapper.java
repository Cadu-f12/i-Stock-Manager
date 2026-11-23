package com.istock.system.mapper;

import com.istock.system.dtos.request.RegisterMovementRequest;
import com.istock.system.dtos.response.MovementPastilleDTO;
import com.istock.system.dtos.response.MovementReasonDTO;
import com.istock.system.dtos.response.MovementResponseDTO;
import com.istock.system.dtos.response.MovementUserDTO;
import com.istock.system.entities.Movement;
import com.istock.system.entities.ReasonMovement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MovementMapper {

    public List<MovementResponseDTO> toResponseDTO(List<Movement> movements) {
        List<MovementResponseDTO> movementsDTO = new ArrayList<>();

        for (Movement movement : movements) {
            movementsDTO.add(toMovementResponseDTO(movement));
        }

        return movementsDTO;
    }

    public MovementResponseDTO toMovementResponseDTO(Movement movement) {
        MovementPastilleDTO pastille = new MovementPastilleDTO(
                movement.getPastille().getId(),
                movement.getPastille().getSkuCode(),
                movement.getPastille().getDescription()
        );
        MovementReasonDTO reason = toMovementReasonDTO(movement.getReasonMovement());
        MovementUserDTO user = new MovementUserDTO(
                movement.getUser().getId(),
                movement.getUser().getName()
        );

        return new MovementResponseDTO(
                movement.getId(),
                movement.getQuantity(),
                movement.getDateTime(),
                pastille,
                reason,
                user
        );
    }

    public MovementReasonDTO toMovementReasonDTO(ReasonMovement reasonMovement) {
        return new MovementReasonDTO(
                reasonMovement.getId(),
                reasonMovement.getDescription(),
                reasonMovement.getType()
        );
    }
}
