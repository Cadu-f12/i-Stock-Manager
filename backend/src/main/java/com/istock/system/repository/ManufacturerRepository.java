package com.istock.system.repository;

import com.istock.system.entities.Manufacturer;
import com.istock.system.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManufacturerRepository
        extends JpaRepository<Manufacturer, Long> {

    Optional<Manufacturer> findByName(String name);

}