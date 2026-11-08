package com.istock.system.controller;

import com.istock.system.dtos.request.ManufacturerRequestDTO;
import com.istock.system.dtos.request.UserRequestDTO;
import com.istock.system.dtos.response.ManufacturerResponseDTO;
import com.istock.system.service.ManufacturerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manufacturer")
public class ManufacturerController {

    private final ManufacturerService manufacturerService;

    public ManufacturerController(ManufacturerService manufacturerService) {
        this.manufacturerService = manufacturerService;
    }

    @GetMapping("/{id}")
    public ManufacturerResponseDTO getManufacturer(@PathVariable Long id) {
        return manufacturerService.getManufacturer(id);
    }

    @GetMapping
    public List<ManufacturerResponseDTO> getAllManufacturers() {
        return manufacturerService.getAllManufacturers();
    }

    @PostMapping("/createManufacturer")
    public ResponseEntity<ManufacturerResponseDTO> createManufacturer(@RequestBody ManufacturerRequestDTO dto) {
        ManufacturerResponseDTO response =  manufacturerService.createManufacturer(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/fetchManufacturer")
    public ManufacturerResponseDTO fetchUser(@RequestBody UserRequestDTO dto) {
        return manufacturerService.fetchManufacturer(dto.email());
    }

    @PutMapping("/updateManufacturer/{id}")
    public ManufacturerResponseDTO updateManufacturer(@PathVariable Long id, @RequestBody ManufacturerRequestDTO dto) {

        return manufacturerService.updateManufacturer(id, dto);
    }

    @DeleteMapping("/deleteManufacturer/{id}")
    public ManufacturerResponseDTO deleteManufacturer(@PathVariable Long id) {

        return manufacturerService.deleteManufacturer(id);
    }
}
