package com.istock.system.service;

import com.istock.system.dtos.response.ReasonMovementDTO;
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
    public List<ReasonMovementDTO> findAll() {
        return reasonMovementRepository.findAll()
                .stream()
                .map(reasonMovementMapper::toReasonMovementDTO)
                .toList();
    }
}
