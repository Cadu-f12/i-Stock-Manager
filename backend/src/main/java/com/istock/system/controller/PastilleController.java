package com.istock.system.controller;

import com.istock.system.dtos.response.PastilleDTO;
import com.istock.system.service.PastilleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
