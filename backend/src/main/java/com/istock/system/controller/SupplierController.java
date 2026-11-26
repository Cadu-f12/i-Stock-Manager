package com.istock.system.controller;

import com.istock.system.dtos.request.SupplierRequestDTO;
import com.istock.system.dtos.response.ApiError;
import com.istock.system.dtos.response.SupplierResponseDTO;
import com.istock.system.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/supplier")
@Tag(name = "Suppliers", description = "Supplier contacts and pastille associations")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a supplier by ID")
    @ApiResponse(responseCode = "200", description = "Supplier returned")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Supplier not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    public SupplierResponseDTO getManufacturer(
            @Parameter(description = "Supplier ID", example = "1") @PathVariable Long id
    ) {
        return supplierService.getSupplier(id);
    }

    @GetMapping
    @Operation(summary = "List suppliers")
    @ApiResponse(responseCode = "200", description = "Suppliers returned")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    public List<SupplierResponseDTO> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    @PostMapping("/createSupplier")
    @Operation(summary = "Create a supplier")
    @ApiResponse(responseCode = "201", description = "Supplier created")
    @ApiResponse(responseCode = "400", description = "One or more pastilles were not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "Authentication required")
    public ResponseEntity<SupplierResponseDTO> createSupplier(@RequestBody SupplierRequestDTO dto) {
        SupplierResponseDTO response =  supplierService.createSupplier(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/fetchSupplier")
    @Operation(summary = "Find a supplier by name", description = "Uses the name from the request body to find a supplier.")
    @ApiResponse(responseCode = "200", description = "Supplier returned")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Supplier not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    public SupplierResponseDTO fetchManuf(@RequestBody SupplierRequestDTO dto) {
        return supplierService.fetchSupplier(dto.name());
    }

    @PutMapping("/updateSupplier/{id}")
    @Operation(summary = "Update a supplier")
    @ApiResponse(responseCode = "200", description = "Supplier updated")
    @ApiResponse(responseCode = "400", description = "One or more pastilles were not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Supplier not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    public SupplierResponseDTO updateManufacturer(@PathVariable Long id, @RequestBody SupplierRequestDTO dto) {

        return supplierService.updateSupplier(id, dto);
    }

    @DeleteMapping("/deleteSupplier/{id}")
    @Operation(summary = "Delete a supplier")
    @ApiResponse(responseCode = "200", description = "Supplier deleted")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Supplier not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "409", description = "Supplier still has linked pastilles")
    public SupplierResponseDTO deleteSupplier(@PathVariable Long id) {

        return supplierService.deleteSupplier(id);
    }
}
