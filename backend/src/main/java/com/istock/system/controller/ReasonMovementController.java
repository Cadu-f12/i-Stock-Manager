package com.istock.system.controller;

import com.istock.system.dtos.request.CreateReasonMovementRequest;
import com.istock.system.dtos.request.UpdateReasonMovementRequest;
import com.istock.system.dtos.response.ApiError;
import com.istock.system.dtos.response.ReasonMovementDTO;
import com.istock.system.service.ReasonMovementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reason-movement")
@Tag(name = "Movement Reasons", description = "Reasons assigned to inventory movements")
public class ReasonMovementController {

    private final ReasonMovementService reasonMovementService;

    public ReasonMovementController(ReasonMovementService reasonMovementService) {
        this.reasonMovementService = reasonMovementService;
    }

    @GetMapping
    @Operation(summary = "List movement reasons", description = "Optionally filter reasons by active status.")
    @ApiResponse(responseCode = "200", description = "Movement reasons returned")
    @ApiResponse(responseCode = "400", description = "Invalid active status filter", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "Authentication required")
    public List<ReasonMovementDTO> findAll(
            @Parameter(description = "Filter by active status. Omit to return all reasons.", example = "true")
            @RequestParam(name = "is_active", required = false) Boolean isActive
    ) {
        return reasonMovementService.findAll(isActive);
    }

    @PostMapping
    @Operation(summary = "Create a movement reason")
    @ApiResponse(responseCode = "201", description = "Movement reason created")
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "Authentication required")
    public ResponseEntity<ReasonMovementDTO> create(
            @RequestBody @Valid CreateReasonMovementRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reasonMovementService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a movement reason by ID")
    @ApiResponse(responseCode = "200", description = "Movement reason returned")
    @ApiResponse(responseCode = "400", description = "Invalid movement reason ID", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Movement reason not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ReasonMovementDTO findById(
            @Parameter(description = "Movement reason ID", example = "1")
            @PathVariable Long id
    ) {
        return reasonMovementService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a movement reason")
    @ApiResponse(responseCode = "200", description = "Movement reason updated")
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Movement reason not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ReasonMovementDTO updateById(
            @Parameter(description = "Movement reason ID", example = "1")
            @PathVariable Long id,
            @RequestBody @Valid UpdateReasonMovementRequest request
    ) {
        return reasonMovementService.updateById(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate a movement reason")
    @ApiResponse(responseCode = "204", description = "Movement reason deactivated")
    @ApiResponse(responseCode = "400", description = "Invalid movement reason ID", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Movement reason not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<Void> deactivateById(
            @Parameter(description = "Movement reason ID", example = "1")
            @PathVariable Long id
    ) {
        reasonMovementService.deactivateById(id);
        return ResponseEntity.noContent().build();
    }
}
