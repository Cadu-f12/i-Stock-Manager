package com.istock.system.controller;

import com.istock.system.dtos.response.ApiError;
import com.istock.system.dtos.response.MovementReasonDTO;
import org.springframework.dao.DataIntegrityViolationException;

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@Tag(name = "Movements")
@RestController
@RequestMapping("/movement")
public class MovementsController {

    private final MovementService movementService;

    public MovementsController(MovementService movementService) {
        this.movementService = movementService;
    }

    @GetMapping
    @Operation(summary = "List movements by pastille, user, type or date range")
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
    @Operation(summary = "Find a movement by ID")
    public ResponseEntity<MovementResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(movementService.findById(id));
    }

    @GetMapping("/reasons")
    @Operation(summary = "List active movement reasons")
    public List<MovementReasonDTO> activeReasons() {
        return movementService.activeReasons();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> movementConflict() {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(409, "MOVEMENT_CONFLICT",
                "The movement could not be saved because of a database constraint."));
    }

    @PostMapping
    @Operation(summary = "Record a movement and update the stock balance")
    public ResponseEntity<MovementResponseDTO> registerMovement(@RequestBody @Valid RegisterMovementRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movementService.registerMovement(request, authentication));
    }
}
