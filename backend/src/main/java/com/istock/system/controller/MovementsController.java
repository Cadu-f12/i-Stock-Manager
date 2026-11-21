package com.istock.system.controller;

import com.istock.system.dtos.request.RegisterMovementRequest;
import com.istock.system.dtos.response.MovementResponseDTO;
import com.istock.system.entities.MovementType;
import com.istock.system.service.MovementService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/movement")
public class MovementsController {

    private final MovementService movementService;

    public MovementsController(MovementService movementService) {
        this.movementService = movementService;
    }

    @GetMapping
    public ResponseEntity<List<MovementResponseDTO>> findAll(
            @RequestParam(name = "pastille_id", required = false) Long pastilleId,
            @RequestParam(name = "user_id", required = false) Long userId,
            @RequestParam(name = "type", required = false) MovementType type,
            @RequestParam(name = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(name = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(movementService.findAll(pastilleId, userId, type, from, to));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovementResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(movementService.findById(id));
    }

    @PostMapping
    public ResponseEntity<MovementResponseDTO> registerMovement(@RequestBody @Valid RegisterMovementRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movementService.registerMovement(request, authentication));
    }
}
