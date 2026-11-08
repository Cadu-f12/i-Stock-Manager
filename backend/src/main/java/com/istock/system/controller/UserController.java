package com.istock.system.controller;

import com.istock.system.dtos.request.UserRequestDTO;
import com.istock.system.dtos.response.UserResponseDTO;
import com.istock.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Tag(name = "Users", description = "User account operations")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @ApiResponse(responseCode = "200", description = "User returned")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    public UserResponseDTO getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @GetMapping
    @Operation(summary = "List users")
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/createUser")
    @Operation(summary = "Create user")
    @ApiResponse(responseCode = "201", description = "User created")
    @ApiResponse(responseCode = "409", description = "User already exists")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO dto) {
        UserResponseDTO response = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/fetchUser")
    @Operation(summary = "Find user by email")
    public UserResponseDTO fetchUser(@RequestBody UserRequestDTO dto) {
        return userService.fetchUser(dto.email());
    }

    @PutMapping("/updateUser/{id}")
    @Operation(summary = "Update user")
    public UserResponseDTO updateUser(@PathVariable Long id, @RequestBody UserRequestDTO dto) {
        return userService.updateUser(id, dto);
    }

    @DeleteMapping("/deleteUser/{id}")
    @Operation(summary = "Delete user")
    public UserResponseDTO deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}
