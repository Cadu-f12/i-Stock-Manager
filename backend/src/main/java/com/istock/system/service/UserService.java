package com.istock.system.service;

import com.istock.system.dtos.request.UserRequestDTO;
import com.istock.system.dtos.response.UserResponseDTO;
import com.istock.system.entities.User;
import com.istock.system.exception.UserAlreadyExist;
import com.istock.system.exception.UserNotFound;
import com.istock.system.exception.UserNotFoundWithEmail;
import com.istock.system.mapper.UserMapper;
import com.istock.system.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO getUser(Long id) {
        User user = userRepository.findById(id) .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "User not found"
        ));
        return userMapper.toResponseDTO(user);
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }

    public UserResponseDTO createUser(UserRequestDTO dto) {

        User user = userMapper.toEntity(dto);

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExist(user);
        }

        user.setPassword(passwordEncoder.encode(dto.password()));

        user.setActive(true);

        User savedUser = userRepository.save(user);

        return userMapper.toResponseDTO(savedUser);
    }

    public UserResponseDTO fetchUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundWithEmail(email));
        return userMapper.toResponseDTO(user);
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFound(id));

        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(dto.password() );

        User savedUser = userRepository.save(user);

        return userMapper.toResponseDTO(savedUser);
    }

        public UserResponseDTO deleteUser(Long id) {

            User user = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFound(id));

            UserResponseDTO response = userMapper.toResponseDTO(user);

            userRepository.delete(user);

            return response;
        }
}
