package com.istock.system.mapper;

import com.istock.system.dtos.request.UserRequestDTO;
import com.istock.system.dtos.response.UserResponseDTO;
import com.istock.system.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

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
                user.getMovements()
        );
    }
}