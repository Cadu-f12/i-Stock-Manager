package com.istock.system.service;

import com.istock.system.dtos.request.RegistryPastilleRequest;
import com.istock.system.dtos.request.UpdatePastilleRequest;
import com.istock.system.dtos.response.*;
import com.istock.system.entities.Manufacturer;
import com.istock.system.entities.Pastille;
import com.istock.system.exception.ManufacturerNotFound;
import com.istock.system.exception.PastilleAlreadyExists;
import com.istock.system.exception.PastilleNotFound;
import com.istock.system.mapper.PastilleMapper;
import com.istock.system.repository.MovementRepository;
import com.istock.system.repository.ManufacturerRepository;
import com.istock.system.repository.PastilleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.istock.system.dtos.request.CategorySelection;
import com.istock.system.repository.PastilleCategoryRepository;
import com.istock.system.entities.Supplier;
import com.istock.system.repository.SupplierRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PastilleService {

    private final PastilleRepository pastilleRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final SupplierRepository supplierRepository;
    private final PastilleCategoryRepository categories;
    private final MovementRepository movements;
    private final PastilleMapper mapper;

    public PastilleService(
            PastilleRepository pastilleRepository,
            ManufacturerRepository manufacturerRepository,
            SupplierRepository supplierRepository,
            PastilleCategoryRepository categories,
            MovementRepository movements,
            PastilleMapper mapper
    ) {
        this.pastilleRepository = pastilleRepository;
        this.manufacturerRepository = manufacturerRepository;
        this.supplierRepository = supplierRepository;
        this.categories = categories;
        this.movements = movements;
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
                new ArrayList<>(),
                new ArrayList<>()
        ));

        updateCategory(pastille, body.category());
        updateSuppliers(pastille, body.supplierIds());
        return mapper.toListDTO(pastille);
    }

    private void updateCategory(Pastille pastille, CategorySelection selection) {
        if (selection == null) return;
        pastille.setCategory(selection.id() == null ? null : categories.findById(selection.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria não encontrada.")));
    }

    private void updateSuppliers(Pastille pastille, Set<Long> supplierIds) {
        if (supplierIds == null) return;
        List<Supplier> suppliers = supplierRepository.findAllById(supplierIds);
        if (suppliers.size() != supplierIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fornecedor não encontrado.");
        }
        pastille.getSuppliers().clear();
        pastille.getSuppliers().addAll(suppliers);
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

        updateCategory(pastille, pastilleRequest.category());
        updateSuppliers(pastille, pastilleRequest.supplierIds());
        pastille.setDescription(pastilleRequest.description());
        pastille.setManufacturer(manufacturer);
        pastille.setMinimumStockLevel(pastilleRequest.minimumStockLevel());

        return mapper.toDTO(pastille);
    }

    @Transactional
    public void deleteById(Long id) {
        Pastille pastille = pastilleRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new PastilleNotFound("Pastille with ID '" + id + "' was not found."));
        if (pastille.getSuppliers() != null) {
            pastille.getSuppliers().clear();
            pastilleRepository.flush();
        }
        movements.deleteAllByPastilleId(id);
        pastilleRepository.delete(pastille);
    }
}
