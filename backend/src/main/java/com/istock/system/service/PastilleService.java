package com.istock.system.service;

import com.istock.system.dtos.request.RegistryPastilleRequest;
import com.istock.system.dtos.request.UpdatePastilleRequest;
import com.istock.system.dtos.response.*;
import com.istock.system.entities.Manufacturer;
import com.istock.system.entities.Pastille;
import com.istock.system.entities.Supplier;
import com.istock.system.exception.ManufacturerNotFound;
import com.istock.system.exception.PastilleAlreadyExists;
import com.istock.system.exception.PastilleNotFound;
import com.istock.system.exception.SupplierNotFound;
import com.istock.system.mapper.PastilleMapper;
import com.istock.system.repository.ManufacturerRepository;
import com.istock.system.repository.PastilleRepository;
import com.istock.system.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PastilleService {

    private final PastilleRepository pastilleRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final SupplierRepository supplierRepository;
    private final PastilleMapper mapper;

    public PastilleService(
            PastilleRepository pastilleRepository,
            ManufacturerRepository manufacturerRepository,
            SupplierRepository supplierRepository,
            PastilleMapper mapper
    ) {
        this.pastilleRepository = pastilleRepository;
        this.manufacturerRepository = manufacturerRepository;
        this.supplierRepository = supplierRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<PastilleListDTO> findAll(Boolean onAlert) {
        List<Pastille> pastilles;

        if (Boolean.TRUE.equals(onAlert)) {
            pastilles = pastilleRepository.findAllOnAlert();
        } else {
            pastilles = pastilleRepository.findAll();
        }

        List<PastilleListDTO> pastilleList = new ArrayList<>();
        for (Pastille pastille : pastilles) {
            pastilleList.add(mapper.toListDTO(pastille));
        }

        return pastilleList;
    }

    @Transactional
    public PastilleListDTO registerPastille(RegistryPastilleRequest body) {
        if (pastilleRepository.existsBySkuCode(body.skuCode())) {
            throw new PastilleAlreadyExists(
                    "A pastille with SKU code '" + body.skuCode() + "' already exists."
            );
        }

        Manufacturer manufacturer = manufacturerRepository.findById(body.manufacturerId())
                .orElseThrow(() -> new ManufacturerNotFound(
                        "Manufacturer with ID '" + body.manufacturerId() + "' was not found."
                ));

        Pastille pastille = pastilleRepository.save(new Pastille(
                body.skuCode(),
                body.description(),
                body.minimumStockLevel(),
                0,
                manufacturer,
                findSuppliers(body.supplierIds()),
                new ArrayList<>()
        ));

        return mapper.toListDTO(pastille);
    }

    @Transactional(readOnly = true)
    public PastilleDTO findById(Long id) {
        Pastille pastille = pastilleRepository.findById(id)
                .orElseThrow(() -> new PastilleNotFound(
                        "Pastille with ID '" + id + "' was not found."
                ));

        return mapper.toDTO(pastille);
    }

    @Transactional
    public PastilleDTO updateById(Long id, UpdatePastilleRequest pastilleRequest) {
        Pastille pastille = pastilleRepository.findById(id)
                .orElseThrow(() -> new PastilleNotFound(
                        "Pastille with ID '" + id + "' was not found."
                ));
        Manufacturer manufacturer = manufacturerRepository.findById(pastilleRequest.manufacturerId())
                .orElseThrow(() -> new ManufacturerNotFound(
                        "Manufacturer with ID '" + pastilleRequest.manufacturerId() + "' was not found."
                ));

        pastille.setDescription(pastilleRequest.description());
        pastille.setManufacturer(manufacturer);
        pastille.setMinimumStockLevel(pastilleRequest.minimumStockLevel());
        pastille.setSuppliers(findSuppliers(pastilleRequest.supplierIds()));

        return mapper.toDTO(pastille);
    }

    private List<Supplier> findSuppliers(List<Long> supplierIds) {
        if (supplierIds == null || supplierIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> distinctIds = supplierIds.stream().distinct().toList();
        List<Supplier> suppliers = supplierRepository.findAllById(distinctIds);
        if (suppliers.size() != distinctIds.size()) {
            throw new SupplierNotFound("One or more supplier IDs were not found.");
        }
        return new ArrayList<>(suppliers);
    }
}
