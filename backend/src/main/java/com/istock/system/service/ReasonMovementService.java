package com.istock.system.service;

import com.istock.system.dtos.request.CreateReasonMovementRequest;
import com.istock.system.dtos.request.UpdateReasonMovementRequest;
import com.istock.system.dtos.response.ReasonMovementDTO;
import com.istock.system.entities.ReasonMovement;
import com.istock.system.exception.ReasonMovementNotFound;
import com.istock.system.repository.ReasonMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReasonMovementService {
    private final ReasonMovementRepository reasons;

    public ReasonMovementService(ReasonMovementRepository reasons) {
        this.reasons = reasons;
    }

    public List<ReasonMovementDTO> findAll(Boolean active) {
        var matches = active == null ? reasons.findAll() : reasons.findAllByIsActive(active);
        return matches.stream().map(ReasonMovementDTO::from).toList();
    }

    public ReasonMovementDTO findById(Long id) {
        return ReasonMovementDTO.from(requireReason(id));
    }

    @Transactional
    public ReasonMovementDTO create(CreateReasonMovementRequest request) {
        return ReasonMovementDTO.from(reasons.save(new ReasonMovement(true, request.type(), request.description())));
    }

    @Transactional
    public ReasonMovementDTO update(Long id, UpdateReasonMovementRequest request) {
        var reason = requireReason(id);
        reason.setDescription(request.description());
        reason.setActive(request.isActive());
        return ReasonMovementDTO.from(reason);
    }

    @Transactional
    public void deactivate(Long id) {
        requireReason(id).setActive(false);
    }

    private ReasonMovement requireReason(Long id) {
        return reasons.findById(id).orElseThrow(() ->
                new ReasonMovementNotFound("Movement reason with ID '" + id + "' was not found."));
    }
}
