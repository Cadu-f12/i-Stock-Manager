package com.istock.system.repository;

import com.istock.system.entities.PastilleCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PastilleCategoryRepository extends JpaRepository<PastilleCategory, Long> {
    List<PastilleCategory> findAllByOrderByNameAsc();
    Optional<PastilleCategory> findByNormalizedName(String normalizedName);
}
