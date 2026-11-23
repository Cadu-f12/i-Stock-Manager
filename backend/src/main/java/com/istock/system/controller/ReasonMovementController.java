package com.istock.system.controller;

import com.istock.system.dtos.response.ReasonMovementDTO;
import com.istock.system.service.ReasonMovementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reason-movement")
public class ReasonMovementController {

    private final ReasonMovementService reasonMovementService;

    public ReasonMovementController(ReasonMovementService reasonMovementService) {
        this.reasonMovementService = reasonMovementService;
    }

    @GetMapping
    public List<ReasonMovementDTO> findAll() {
        return reasonMovementService.findAll();
    }
}
