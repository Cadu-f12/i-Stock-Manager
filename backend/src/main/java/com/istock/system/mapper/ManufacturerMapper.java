package com.istock.system.mapper;

import com.istock.system.dtos.request.ManufacturerRequestDTO;
import com.istock.system.dtos.response.ManufacturerResponseDTO;
import com.istock.system.entities.Manufacturer;
import org.springframework.stereotype.Component;

@Component
public class ManufacturerMapper {

    public Manufacturer toEntity(ManufacturerRequestDTO dto) {

        Manufacturer manufacturer = new Manufacturer();

        manufacturer.setPastilles(dto.pastilles());
        manufacturer.setName(dto.name());
        manufacturer.setContact(dto.contact());
        manufacturer.setActive(dto.isActive());

        return manufacturer;
    }

    public ManufacturerResponseDTO toResponseDTO(Manufacturer manufacturer) {

        return new ManufacturerResponseDTO(
                manufacturer.getId(),
                manufacturer.getName(),
                manufacturer.getContact(),
                manufacturer.getActive(),
                manufacturer.getPastilles()
        );
    }
}