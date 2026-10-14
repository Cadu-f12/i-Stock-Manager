package com.istock.system.repository;

import com.istock.system.entities.Movement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovementRepository extends JpaRepository<Long, Movement> {
}
