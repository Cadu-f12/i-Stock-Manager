package com.istock.system.service;

import com.istock.system.dtos.response.MovementResponseDTO;
import com.istock.system.mapper.MovementMapper;
import com.istock.system.repository.MovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MovementService {
    
    private final MovementRepository movementRepository;
    private final MovementMapper mapper;

    public MovementService(MovementRepository movementRepository, MovementMapper mapper) {
        this.movementRepository = movementRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<MovementResponseDTO> findAll() {
        return mapper.toResponseDTO(movementRepository.findAll());
    }
}
