package com.istock.system.controller;

import com.istock.system.dtos.response.MovementResponseDTO;
import com.istock.system.service.MovementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
