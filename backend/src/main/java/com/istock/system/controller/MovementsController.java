package com.istock.system.controller;

import com.istock.system.dtos.request.RegisterMovementRequest;
import com.istock.system.dtos.response.MovementResponseDTO;
import com.istock.system.service.MovementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movement")
public class MovementsController {

    private final MovementService movementService;

    public MovementsController(MovementService movementService) {
        this.movementService = movementService;
    }

    @GetMapping
    public ResponseEntity<List<MovementResponseDTO>> findAll() {
        return ResponseEntity.ok(movementService.findAll());
    }

    @PostMapping
    public ResponseEntity<MovementResponseDTO> registerMovement(@RequestBody @Valid RegisterMovementRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movementService.registerMovement(request, authentication));
    }
}
