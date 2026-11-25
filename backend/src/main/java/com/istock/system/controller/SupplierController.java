package com.istock.system.controller;

import com.istock.system.dtos.request.ManufacturerRequestDTO;
import com.istock.system.dtos.request.SupplierRequestDTO;
import com.istock.system.dtos.response.SupplierResponseDTO;
import com.istock.system.service.SupplierService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@Tag(name = "Suppliers")
@RestController
@RequestMapping("/supplier")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find a supplier by ID")
    public SupplierResponseDTO getManufacturer(@PathVariable Long id) {
        return supplierService.getSupplier(id);
    }

    @GetMapping
    @Operation(summary = "List suppliers")
    public List<SupplierResponseDTO> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    @PostMapping("/createSupplier")
    @Operation(summary = "Create a supplier")
    public ResponseEntity<SupplierResponseDTO> createSupplier(@RequestBody SupplierRequestDTO dto) {
        SupplierResponseDTO response =  supplierService.createSupplier(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/fetchSupplier")
    @Operation(summary = "Find a supplier by name")
    public SupplierResponseDTO fetchManuf(@RequestBody SupplierRequestDTO dto) {
        return supplierService.fetchSupplier(dto.name());
    }

    @PutMapping("/updateSupplier/{id}")
    @Operation(summary = "Update a supplier")
    public SupplierResponseDTO updateManufacturer(@PathVariable Long id, @RequestBody SupplierRequestDTO dto) {

        return supplierService.updateSupplier(id, dto);
    }

    @DeleteMapping("/deleteSupplier/{id}")
    @Operation(summary = "Delete a supplier")
    public SupplierResponseDTO deleteSupplier(@PathVariable Long id) {

        return supplierService.deleteSupplier(id);
    }
}
