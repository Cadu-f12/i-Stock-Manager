package com.istock.system.repository;

import com.istock.system.entities.PastilleCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PastilleCategoryRepository extends JpaRepository<PastilleCategory, Long> {
    interface CategoryRow {
        Long getId();
        String getName();
        long getPastilleCount();
    }

    @org.springframework.data.jpa.repository.Query("""
            SELECT c.id AS id, c.name AS name, COUNT(p.id) AS pastilleCount
            FROM PastilleCategory c LEFT JOIN Pastille p ON p.category = c
            GROUP BY c.id, c.name ORDER BY c.name
            """)
    List<CategoryRow> listWithUsage();

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("SELECT c FROM PastilleCategory c WHERE c.id = :id")
    Optional<PastilleCategory> findForDeletion(@org.springframework.data.repository.query.Param("id") Long id);
    Optional<PastilleCategory> findByNormalizedName(String normalizedName);
}
