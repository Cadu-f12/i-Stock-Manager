package com.istock.system.service;

import com.istock.system.dtos.request.RegisterMovementRequest;
import com.istock.system.dtos.response.MovementResponseDTO;
import com.istock.system.entities.Movement;
import com.istock.system.entities.Pastille;
import com.istock.system.entities.ReasonMovement;
import com.istock.system.entities.User;
import com.istock.system.exception.PastilleNotFound;
import com.istock.system.exception.UserNotFound;
import com.istock.system.exception.UserNotFoundWithEmail;
import com.istock.system.mapper.MovementMapper;
import com.istock.system.repository.MovementRepository;
import com.istock.system.repository.PastilleRepository;
import com.istock.system.repository.ReasonMovementRepository;
import com.istock.system.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovementService {

    private final PastilleRepository pastilleRepository;
    private final ReasonMovementRepository reasonMovementRepository;
    private final UserRepository userRepository;
    private final MovementRepository movementRepository;
    private final MovementMapper mapper;

    public MovementService(PastilleRepository pastilleRepository, ReasonMovementRepository reasonMovementRepository, UserRepository userRepository, MovementRepository movementRepository, MovementMapper mapper) {
        this.pastilleRepository = pastilleRepository;
        this.reasonMovementRepository = reasonMovementRepository;
        this.userRepository = userRepository;
        this.movementRepository = movementRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<MovementResponseDTO> findAll() {
        return mapper.toResponseDTO(movementRepository.findAll());
    }

    @Transactional
    public MovementResponseDTO registerMovement(RegisterMovementRequest request, Authentication authentication) {
        Pastille pastille = pastilleRepository.findById(request.pastille())
            .orElseThrow(() -> new PastilleNotFound("")); //TODO: Create a 404 error here
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UserNotFoundWithEmail(authentication.getName()));

        ReasonMovement reason = new ReasonMovement(
                Boolean.TRUE,
                request.type(),
                request.description()
        );
        Movement movement = new Movement(
                pastille,
                user,
                reason,
                request.quantity(),
                LocalDateTime.now()
         );

        reason = reasonMovementRepository.save(reason);
        movement = movementRepository.save(movement);

        return mapper.toMovementResponseDTO(movement);
    }
}
