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
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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

    public SupplierResponseDTO createSupplier(SupplierRequestDTO dto) {

        Supplier supplier = supplierMapper.toEntity(dto);

        supplier.setActive(true);

        supplier.setPastilles(findPastilles(dto.pastilles()));

        Supplier savedSupplier = supplierRepository.save(supplier);

        return supplierMapper.toResponseDTO(savedSupplier);
    }

    public SupplierResponseDTO fetchSupplier(String name) {

        Supplier supplier = supplierRepository.findByName(name)
                .orElseThrow(() -> new UserNotFoundWithEmail(name));

        return supplierMapper.toResponseDTO(supplier);
    }

    public SupplierResponseDTO updateSupplier(Long id, SupplierRequestDTO dto) {

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new UserNotFound(id));

        supplier.setName(dto.name());
        supplier.setContact(dto.contact());

        supplier.setPastilles(findPastilles(dto.pastilles()));

        Supplier savedSupplier = supplierRepository.save(supplier);

        return supplierMapper.toResponseDTO(savedSupplier);
    }

    public SupplierResponseDTO deleteSupplier(Long id) {

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new UserNotFound(id));

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
}