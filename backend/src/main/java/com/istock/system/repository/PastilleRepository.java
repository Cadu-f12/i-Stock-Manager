package com.istock.system.repository;

import com.istock.system.entities.Pastille;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PastilleRepository extends JpaRepository<Pastille, Long> {
    @Override
    @EntityGraph(attributePaths = {"manufacturer", "suppliers", "category"})
    List<Pastille> findAll();

    @EntityGraph(attributePaths = {"manufacturer", "suppliers", "category"})
    @Query("""
        SELECT p
        FROM Pastille p
        WHERE p.currentBalance <= p.minimumStockLevel + 5
    """)
    List<Pastille> findAllOnAlert();

    boolean existsByCategoryId(Long categoryId);

    Boolean existsBySkuCode(String skuCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Pastille p WHERE p.id = :id")
    Optional<Pastille> findByIdForUpdate(@Param("id") Long id);

    @Override
    Optional<Pastille> findById(Long id);
}
