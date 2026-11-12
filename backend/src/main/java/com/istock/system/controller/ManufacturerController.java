package com.istock.system.controller;

import com.istock.system.dtos.request.ManufacturerRequestDTO;
import com.istock.system.dtos.response.ManufacturerResponseDTO;
import com.istock.system.service.ManufacturerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manufacturer")
@Tag(name = "Manufacturers", description = "Manufacturer records and contacts")
public class ManufacturerController {

    private final ManufacturerService manufacturerService;

    public ManufacturerController(ManufacturerService manufacturerService) {
        this.manufacturerService = manufacturerService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get manufacturer by ID")
    @ApiResponse(responseCode = "200", description = "Manufacturer returned")
    @ApiResponse(responseCode = "404", description = "Manufacturer not found", content = @Content(schema = @Schema(implementation = com.istock.system.dtos.response.ApiError.class)))
    public ManufacturerResponseDTO getManufacturer(@PathVariable Long id) {
        return manufacturerService.getManufacturer(id);
    }

    @GetMapping
    @Operation(summary = "List manufacturers")
    @ApiResponse(responseCode = "200", description = "Manufacturers returned")
    public List<ManufacturerResponseDTO> getAllManufacturers() {
        return manufacturerService.getAllManufacturers();
    }

    @PostMapping("/createManufacturer")
    @Operation(summary = "Create manufacturer")
    @ApiResponse(responseCode = "201", description = "Manufacturer created")
    public ResponseEntity<ManufacturerResponseDTO> createManufacturer(@RequestBody ManufacturerRequestDTO dto) {
        ManufacturerResponseDTO response =  manufacturerService.createManufacturer(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/fetchManufacturer")
    @Operation(summary = "Find manufacturer by name")
    @ApiResponse(responseCode = "200", description = "Manufacturer returned")
    @ApiResponse(responseCode = "404", description = "Manufacturer not found", content = @Content(schema = @Schema(implementation = com.istock.system.dtos.response.ApiError.class)))
    public ManufacturerResponseDTO fetchManufacturer(@RequestBody ManufacturerRequestDTO dto) {
        return manufacturerService.fetchManufacturer(dto.name());
    }

    @PutMapping("/updateManufacturer/{id}")
    @Operation(summary = "Update manufacturer")
    @ApiResponse(responseCode = "200", description = "Manufacturer updated")
    @ApiResponse(responseCode = "404", description = "Manufacturer not found", content = @Content(schema = @Schema(implementation = com.istock.system.dtos.response.ApiError.class)))
    public ManufacturerResponseDTO updateManufacturer(@PathVariable Long id, @RequestBody ManufacturerRequestDTO dto) {

        return manufacturerService.updateManufacturer(id, dto);
    }

    @DeleteMapping("/deleteManufacturer/{id}")
    @Operation(summary = "Delete manufacturer")
    @ApiResponse(responseCode = "200", description = "Manufacturer deleted")
    @ApiResponse(responseCode = "404", description = "Manufacturer not found", content = @Content(schema = @Schema(implementation = com.istock.system.dtos.response.ApiError.class)))
    public ManufacturerResponseDTO deleteManufacturer(@PathVariable Long id) {

        return manufacturerService.deleteManufacturer(id);
    }
}
