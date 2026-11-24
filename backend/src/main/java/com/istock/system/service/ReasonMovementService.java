package com.istock.system.service;

import com.istock.system.dtos.request.CreateReasonMovementRequest;
import com.istock.system.dtos.request.UpdateReasonMovementRequest;
import com.istock.system.dtos.response.ReasonMovementDTO;
import com.istock.system.entities.ReasonMovement;
import com.istock.system.exception.ReasonMovementNotFound;
import com.istock.system.mapper.ReasonMovementMapper;
import com.istock.system.repository.ReasonMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReasonMovementService {

    private final ReasonMovementRepository reasonMovementRepository;
    private final ReasonMovementMapper reasonMovementMapper;

    public ReasonMovementService(
            ReasonMovementRepository reasonMovementRepository,
            ReasonMovementMapper reasonMovementMapper
    ) {
        this.reasonMovementRepository = reasonMovementRepository;
        this.reasonMovementMapper = reasonMovementMapper;
    }

    @Transactional(readOnly = true)
    public List<ReasonMovementDTO> findAll(Boolean isActive) {
        return (isActive == null
                ? reasonMovementRepository.findAll()
                : reasonMovementRepository.findAllByIsActive(isActive))
                .stream()
                .map(reasonMovementMapper::toReasonMovementDTO)
                .toList();
    }

    @Transactional
    public ReasonMovementDTO create(CreateReasonMovementRequest request) {
        ReasonMovement reasonMovement = reasonMovementMapper.toEntity(request);
        return reasonMovementMapper.toReasonMovementDTO(reasonMovementRepository.save(reasonMovement));
    }

    @Transactional
    public ReasonMovementDTO updateById(Long id, UpdateReasonMovementRequest request) {
        ReasonMovement reasonMovement = requireReasonMovement(id);
        reasonMovementMapper.updateEntity(reasonMovement, request);
        return reasonMovementMapper.toReasonMovementDTO(reasonMovementRepository.save(reasonMovement));
    }

    @Transactional
    public void deactivateById(Long id) {
        ReasonMovement reasonMovement = requireReasonMovement(id);
        reasonMovement.setActive(false);
        reasonMovementRepository.save(reasonMovement);
    }

    @Transactional(readOnly = true)
    public ReasonMovementDTO findById(Long id) {
        return reasonMovementMapper.toReasonMovementDTO(requireReasonMovement(id));
    }

    private ReasonMovement requireReasonMovement(Long id) {
        return reasonMovementRepository.findById(id)
                .orElseThrow(() -> new ReasonMovementNotFound(
                        "Movement reason with ID '" + id + "' was not found."
                ));
    }
}
