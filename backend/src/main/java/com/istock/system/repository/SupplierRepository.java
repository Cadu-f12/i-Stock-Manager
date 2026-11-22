package com.istock.system.repository;

import com.istock.system.entities.Supplier;
import com.istock.system.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupplierRepository
        extends JpaRepository<Supplier, Long> {
    Optional<Supplier> findByName(String name);
}
