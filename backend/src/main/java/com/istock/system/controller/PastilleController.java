package com.istock.system.controller;

import com.istock.system.dtos.request.RegistryPastilleRequest;
import com.istock.system.dtos.response.PastilleDTO;
import com.istock.system.service.PastilleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pastilles")
public class PastilleController {

    private final PastilleService pastilleService;

    public PastilleController(PastilleService pastilleService) {
        this.pastilleService = pastilleService;
    }

    @GetMapping
    public ResponseEntity<List<PastilleDTO>> findAll(@RequestParam(name="on_alert", required = false) Boolean onAlert) {
        return ResponseEntity.ok(pastilleService.findAll(onAlert));
    }

    @PostMapping
    public ResponseEntity<PastilleDTO> registerPastille(@RequestBody @Valid RegistryPastilleRequest body) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pastilleService.registerPastille(body));
    }
}
