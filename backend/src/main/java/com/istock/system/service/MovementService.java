package com.istock.system.service;

import com.istock.system.dtos.request.RegisterMovementRequest;
import com.istock.system.dtos.response.MovementReasonDTO;
import com.istock.system.dtos.response.MovementResponseDTO;
import com.istock.system.entities.Movement;
import com.istock.system.entities.MovementType;
import com.istock.system.entities.Pastille;
import com.istock.system.entities.ReasonMovement;
import com.istock.system.entities.User;
import com.istock.system.exception.InsufficientStockException;
import com.istock.system.exception.PastilleNotFound;
import com.istock.system.exception.ReasonMovementNotFound;
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

    @Transactional(readOnly = true)
    public List<MovementReasonDTO> findActiveReasons() {
        return reasonMovementRepository.findAllActive().stream()
                .map(mapper::toMovementReasonDTO)
                .toList();
    }

    @Transactional
    public MovementResponseDTO registerMovement(RegisterMovementRequest request, Authentication authentication) {
        Pastille pastille = pastilleRepository.findByIdForUpdate(request.pastille())
                .orElseThrow(() -> new PastilleNotFound(
                        "Pastille with ID '" + request.pastille() + "' was not found."
                ));
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UserNotFoundWithEmail(authentication.getName()));
        ReasonMovement reason = reasonMovementRepository.findActiveById(request.reasonMovementId())
                .orElseThrow(() -> new ReasonMovementNotFound(
                        "Active movement reason with ID '" + request.reasonMovementId() + "' was not found."
                ));

        if (reason.getType().equals(MovementType.IN)) {
            pastille.increaseBalance(request.quantity());
        } else {
            if (request.quantity() > pastille.getCurrentBalance()) {
                throw new InsufficientStockException(
                        "Requested quantity exceeds available stock for pastille " + pastille.getId() + "."
                );
            }
            pastille.decreaseBalance(request.quantity());
        }

        Movement movement = new Movement(
                pastille,
                user,
                reason,
                request.quantity(),
                LocalDateTime.now()
         );

        movement = movementRepository.save(movement);

        return mapper.toMovementResponseDTO(movement);
    }
}
