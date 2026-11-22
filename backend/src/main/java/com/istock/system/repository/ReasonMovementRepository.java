package com.istock.system.repository;

import com.istock.system.dtos.response.MovementReasonDTO;

import com.istock.system.entities.ReasonMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReasonMovementRepository extends JpaRepository<ReasonMovement, Long> {
    @Query("SELECT new com.istock.system.dtos.response.MovementReasonDTO(r.id, r.description, r.type) FROM ReasonMovement r WHERE r.isActive = true ORDER BY r.description, r.id")
    List<MovementReasonDTO> findActiveReasons();

    @Query("SELECT r FROM ReasonMovement r WHERE r.id = :id AND r.isActive = true")
    Optional<ReasonMovement> findActiveById(@Param("id") Long id);

}
