package com.istock.system.controller;

import com.istock.system.dtos.request.RegisterMovementRequest;
import com.istock.system.dtos.response.ApiError;
import com.istock.system.dtos.response.MovementReasonDTO;
import org.springframework.dao.DataIntegrityViolationException;
import com.istock.system.dtos.response.MovementResponseDTO;
import com.istock.system.entities.MovementType;
import com.istock.system.service.MovementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Movements", description = "Inventory movement history and registration")
public class MovementsController {

    private final MovementService movementService;

    public MovementsController(MovementService movementService) {
        this.movementService = movementService;
    }

    @GetMapping
    @Operation(
            summary = "List movements",
            description = "Optionally filter movements by pastille, user, movement type, and inclusive date range."
    )
    @ApiResponse(responseCode = "200", description = "Movements returned")
    @ApiResponse(responseCode = "400", description = "Invalid filter or date range", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "Authentication required")
    public ResponseEntity<List<MovementResponseDTO>> findAll(
            @Parameter(description = "Filter by pastille ID", example = "1")
            @RequestParam(name = "pastille_id", required = false) Long pastilleId,
            @Parameter(description = "Filter by user ID", example = "1")
            @RequestParam(name = "user_id", required = false) Long userId,
            @Parameter(description = "Filter by movement type: IN or OUT", example = "IN")
            @RequestParam(name = "type", required = false) MovementType type,
            @Parameter(description = "Inclusive start date, in YYYY-MM-DD format", example = "2026-01-01")
            @RequestParam(name = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Inclusive end date, in YYYY-MM-DD format", example = "2026-01-31")
            @RequestParam(name = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(movementService.findAll(pastilleId, userId, type, from, to));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a movement by ID")
    @ApiResponse(responseCode = "200", description = "Movement returned")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Movement not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<MovementResponseDTO> findById(
            @Parameter(description = "Movement ID", example = "1") @PathVariable Long id
    ) {
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
    @Operation(summary = "Register a movement")
    @ApiResponse(responseCode = "201", description = "Movement registered")
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Pastille or active movement reason not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "409", description = "Insufficient stock", content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<MovementResponseDTO> registerMovement(
            @RequestBody @Valid RegisterMovementRequest request,
            @Parameter(hidden = true) Authentication authentication
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movementService.registerMovement(request, authentication));
    }
}
