package com.istock.system.mapper;

import com.istock.system.dtos.request.UserRequestDTO;
import com.istock.system.dtos.response.*;
import com.istock.system.entities.Movement;
import com.istock.system.entities.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    private final ReasonMovementMapper reasonMovementMapper;

    public UserMapper(ReasonMovementMapper reasonMovementMapper) {
        this.reasonMovementMapper = reasonMovementMapper;
    }

    public User toEntity(UserRequestDTO dto) {

        User user = new User();

        user.setRole(dto.role());
        user.setName(dto.name());
        user.setEmail(dto.email());

        return user;
    }

    public UserResponseDTO toResponseDTO(User user) {

        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getActive(),
                toMovementList(user.getMovements())
        );
    }

    private List<MovementDTO> toMovementList(List<Movement> movements) {

        if (movements == null) {
            return List.of();
        }

        return movements.stream()
                .map(movement -> {

                    UserDTO userDTO = new UserDTO(
                            movement.getUser().getId(),
                            movement.getUser().getName()
                    );

                    ReasonMovementDTO reasonDTO = reasonMovementMapper
                            .toReasonMovementDTO(movement.getReasonMovement());

                    return new MovementDTO(
                            movement.getId(),
                            movement.getQuantity(),
                            movement.getDateTime(),
                            reasonDTO,
                            userDTO
                    );
                })
                .toList();
    }
}
