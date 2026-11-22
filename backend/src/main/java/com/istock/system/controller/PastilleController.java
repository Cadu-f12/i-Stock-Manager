package com.istock.system.controller;

import com.istock.system.dtos.request.RegistryPastilleRequest;
import com.istock.system.dtos.request.UpdatePastilleRequest;
import com.istock.system.dtos.response.PastilleDTO;
import com.istock.system.dtos.response.PastilleListDTO;
import com.istock.system.service.PastilleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/pastilles")
@Tag(name = "Pastilles", description = "Cutting inserts and inventory levels")
public class PastilleController {

    private final PastilleService pastilleService;

    public PastilleController(PastilleService pastilleService) {
        this.pastilleService = pastilleService;
    }

    @GetMapping
    @Operation(summary = "List pastilles", description = "Optionally filter pastilles that are below their minimum stock level.")
    @ApiResponse(responseCode = "200", description = "Pastilles returned")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    public ResponseEntity<List<PastilleListDTO>> findAll(@RequestParam(name="on_alert", required = false) Boolean onAlert) {
        return ResponseEntity.ok(pastilleService.findAll(onAlert));
    }

    @PostMapping
    @Operation(summary = "Register a pastille")
    @ApiResponse(responseCode = "201", description = "Pastille registered")
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = com.istock.system.dtos.response.ApiError.class)))
    @ApiResponse(responseCode = "409", description = "SKU code already exists", content = @Content(schema = @Schema(implementation = com.istock.system.dtos.response.ApiError.class)))
    public ResponseEntity<PastilleListDTO> registerPastille(@RequestBody @Valid RegistryPastilleRequest body) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pastilleService.registerPastille(body));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a pastille", description = "Returns a pastille with its stock, manufacturer, suppliers, and movements.")
    @ApiResponse(responseCode = "200", description = "Pastille returned")
    @ApiResponse(responseCode = "404", description = "Pastille not found", content = @Content(schema = @Schema(implementation = com.istock.system.dtos.response.ApiError.class)))
    public ResponseEntity<PastilleDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(pastilleService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a pastille")
    @ApiResponse(responseCode = "200", description = "Pastille updated")
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = com.istock.system.dtos.response.ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Pastille or manufacturer not found", content = @Content(schema = @Schema(implementation = com.istock.system.dtos.response.ApiError.class)))
    public ResponseEntity<PastilleDTO> updateById(@PathVariable Long id, @RequestBody @Valid UpdatePastilleRequest pastilleRequest) {
        return ResponseEntity.ok(pastilleService.updateById(id, pastilleRequest));
    }
}
