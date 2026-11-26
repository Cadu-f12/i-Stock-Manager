package com.istock.system.service;

import com.istock.system.dtos.request.SupplierRequestDTO;
import com.istock.system.dtos.response.PastilleListDTO;
import com.istock.system.dtos.response.SupplierResponseDTO;
import com.istock.system.entities.Pastille;
import com.istock.system.entities.Supplier;
import com.istock.system.exception.UserNotFound;
import com.istock.system.exception.UserNotFoundWithEmail;
import com.istock.system.mapper.SupplierMapper;
import com.istock.system.repository.PastilleRepository;
import com.istock.system.repository.SupplierRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final PastilleRepository pastilleRepository;
    private final SupplierMapper supplierMapper;

    public SupplierService(
            SupplierRepository supplierRepository,
            PastilleRepository pastilleRepository,
            SupplierMapper supplierMapper
    ) {
        this.supplierRepository = supplierRepository;
        this.pastilleRepository = pastilleRepository;
        this.supplierMapper = supplierMapper;
    }

    public SupplierResponseDTO getSupplier(Long id) {

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Supplier not found"
                ));

        return supplierMapper.toResponseDTO(supplier);
    }

    public List<SupplierResponseDTO> getAllSuppliers() {

        return supplierRepository.findAll()
                .stream()
                .map(supplierMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public SupplierResponseDTO createSupplier(SupplierRequestDTO dto) {

        Supplier supplier = supplierMapper.toEntity(dto);
        List<Pastille> pastilles = findPastilles(dto.pastilles());

        supplier.setActive(true);
        supplier.setPastilles(pastilles);

        Supplier savedSupplier = supplierRepository.save(supplier);

        addSupplierToPastilles(pastilles, savedSupplier);
        pastilleRepository.saveAll(pastilles);

        return supplierMapper.toResponseDTO(savedSupplier);
    }

    public SupplierResponseDTO fetchSupplier(String name) {

        Supplier supplier = supplierRepository.findByName(name)
                .orElseThrow(() -> new UserNotFoundWithEmail(name));

        return supplierMapper.toResponseDTO(supplier);
    }

    @Transactional
    public SupplierResponseDTO updateSupplier(Long id, SupplierRequestDTO dto) {

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new UserNotFound(id));
        List<Pastille> previousPastilles = new ArrayList<>(supplier.getPastilles());
        List<Pastille> pastilles = findPastilles(dto.pastilles());

        supplier.setName(dto.name());
        supplier.setContact(dto.contact());
        supplier.setPastilles(pastilles);

        Supplier savedSupplier = supplierRepository.save(supplier);

        removeSupplierFromPastilles(previousPastilles, savedSupplier);
        addSupplierToPastilles(pastilles, savedSupplier);

        Set<Pastille> affectedPastilles = new LinkedHashSet<>(previousPastilles);
        affectedPastilles.addAll(pastilles);
        pastilleRepository.saveAll(affectedPastilles);

        return supplierMapper.toResponseDTO(savedSupplier);
    }

    @Transactional
    public SupplierResponseDTO deleteSupplier(Long id) {

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new UserNotFound(id));

        if (!supplier.getPastilles().isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT,
                    "Remove the pastille links before deleting this supplier.");
        }
        SupplierResponseDTO response = supplierMapper.toResponseDTO(supplier);

        supplierRepository.delete(supplier);

        return response;
    }

    private List<Pastille> findPastilles(List<PastilleListDTO> pastilleDTOs) {

        if (pastilleDTOs == null || pastilleDTOs.isEmpty()) {
            return List.of();
        }

        List<Long> ids = pastilleDTOs.stream()
                .map(PastilleListDTO::id)
                .toList();

        List<Pastille> pastilles = pastilleRepository.findAllById(ids);

        if (pastilles.size() != ids.stream().distinct().count()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "One or more pastilles were not found"
            );
        }

        return pastilles;
    }

    private void addSupplierToPastilles(List<Pastille> pastilles, Supplier supplier) {
        for (Pastille pastille : pastilles) {
            List<Supplier> suppliers = pastille.getSuppliers();
            if (suppliers == null) {
                suppliers = new ArrayList<>();
                pastille.setSuppliers(suppliers);
            }

            boolean isAlreadyAssigned = suppliers.stream()
                    .anyMatch(existingSupplier -> Objects.equals(existingSupplier.getId(), supplier.getId()));
            if (!isAlreadyAssigned) {
                suppliers.add(supplier);
            }
        }
    }

    private void removeSupplierFromPastilles(List<Pastille> pastilles, Supplier supplier) {
        for (Pastille pastille : pastilles) {
            List<Supplier> suppliers = pastille.getSuppliers();
            if (suppliers != null) {
                suppliers.removeIf(existingSupplier ->
                        Objects.equals(existingSupplier.getId(), supplier.getId()));
            }
        }
    }
}
