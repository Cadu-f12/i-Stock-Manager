package com.istock.system.repository;

import com.istock.system.entities.Pastille;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PastilleRepository extends JpaRepository<Pastille, Long> {
    @Query("""
        SELECT p
        FROM Pastille p
        WHERE p.currentBalance <= p.minimumStockLevel + 5
    """)
    List<Pastille> findAllOnAlert();

    Boolean existsBySkuCode(String skuCode);

    @Override
    Optional<Pastille> findById(Long id);
}
