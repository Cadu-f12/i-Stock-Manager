package com.istock.system.controller;

import com.istock.system.dtos.request.CreateReasonMovementRequest;
import com.istock.system.dtos.request.UpdateReasonMovementRequest;
import com.istock.system.dtos.response.ReasonMovementDTO;
import com.istock.system.service.ReasonMovementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@Tag(name = "Movement Reasons")
@RestController
@RequestMapping("/reason-movement")
public class ReasonMovementController {
    private final ReasonMovementService reasons;

    public ReasonMovementController(ReasonMovementService reasons) {
        this.reasons = reasons;
    }

    @GetMapping
    @Operation(summary = "List movement reasons, optionally filtered by active status")
    public List<ReasonMovementDTO> findAll(@RequestParam(name = "is_active", required = false) Boolean active) {
        return reasons.findAll(active);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find a movement reason by ID")
    public ReasonMovementDTO findById(@PathVariable Long id) {
        return reasons.findById(id);
    }

    @PostMapping
    @Operation(summary = "Create an active movement reason")
    @ResponseStatus(HttpStatus.CREATED)
    public ReasonMovementDTO create(@Valid @RequestBody CreateReasonMovementRequest request) {
        return reasons.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Change the description and active status of a reason")
    public ReasonMovementDTO update(@PathVariable Long id, @Valid @RequestBody UpdateReasonMovementRequest request) {
        return reasons.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate a reason and preserve existing movements")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) {
        reasons.deactivate(id);
    }
}
