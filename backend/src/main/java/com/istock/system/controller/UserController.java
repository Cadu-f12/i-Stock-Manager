package com.istock.system.controller;

import com.istock.system.dtos.request.UserRequestDTO;
import com.istock.system.dtos.response.UserResponseDTO;
import com.istock.system.entities.User;
import com.istock.system.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
     }

     @GetMapping("/{id}")
    public UserResponseDTO getUser(@PathVariable Long id) {
         return userService.getUser(id);
     }

     @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

     @PostMapping("/createUser")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO dto) {
         UserResponseDTO response =  userService.createUser(dto);

         return ResponseEntity
                 .status(HttpStatus.CREATED)
                 .body(response);


    }

     @PostMapping("/fetchUser")
    public UserResponseDTO fetchUser(@RequestBody UserRequestDTO dto) {
        return userService.fetchUser(dto.email());
    }

    @PutMapping("/updateUser/{id}")
    public UserResponseDTO updateUser(@PathVariable Long id, @RequestBody UserRequestDTO dto) {

        return userService.updateUser(id, dto);
    }

    @DeleteMapping("/deleteUser/{id}")
    public UserResponseDTO deleteUser(@PathVariable Long id) {

        return userService.deleteUser(id);
    }
}
