package com.istock.system.repository;

import com.istock.system.entities.Movement;
import com.istock.system.entities.MovementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MovementRepository extends JpaRepository<Movement, Long> {

    @Query("""
            SELECT m
            FROM Movement m
            WHERE (:pastilleId IS NULL OR m.pastille.id = :pastilleId)
              AND (:userId IS NULL OR m.user.id = :userId)
              AND (:type IS NULL OR m.reasonMovement.type = :type)
              AND (:startDateTime IS NULL OR m.dateTime >= :startDateTime)
              AND (:endDateTimeExclusive IS NULL OR m.dateTime < :endDateTimeExclusive)
            ORDER BY m.dateTime DESC
            """)
    List<Movement> findByFilters(
            @Param("pastilleId") Long pastilleId,
            @Param("userId") Long userId,
            @Param("type") MovementType type,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTimeExclusive") LocalDateTime endDateTimeExclusive
    );
}
