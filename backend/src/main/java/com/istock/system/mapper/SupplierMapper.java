package com.istock.system.mapper;

import com.istock.system.dtos.request.SupplierRequestDTO;
import com.istock.system.dtos.response.SupplierResponseDTO;
import com.istock.system.entities.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

    private final PastilleMapper pastilleMapper;

    public SupplierMapper(PastilleMapper pastilleMapper) {
        this.pastilleMapper = pastilleMapper;
    }

    public Supplier toEntity(SupplierRequestDTO dto) {
        Supplier supplier = new Supplier();

        supplier.setName(dto.name());
        supplier.setContact(dto.contact());
        supplier.setActive(dto.isActive());

        return supplier;
    }

    public SupplierResponseDTO toResponseDTO(Supplier supplier) {
        return new SupplierResponseDTO(
                supplier.getId(),
                supplier.getName(),
                supplier.getContact(),
                supplier.getActive(),
                supplier.getPastilles()
                        .stream()
                        .map(pastilleMapper::toListDTO)
                        .toList()
        );
    }
}