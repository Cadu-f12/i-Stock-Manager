package com.istock.system.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface InventoryCategoryRepository extends JpaRepository<InventoryCategory, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<InventoryCategory> findByNormalizedName(String normalizedName);
}
