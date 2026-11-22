package com.istock.system.mapper;

import com.istock.system.dtos.request.ManufacturerRequestDTO;
import com.istock.system.dtos.response.ManufacturerResponseDTO;
import com.istock.system.dtos.response.PastilleDTO;
import com.istock.system.dtos.response.PastilleListDTO;
import com.istock.system.entities.Manufacturer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ManufacturerMapper {

    private final PastilleMapper pastilleMapper;

    public ManufacturerMapper(PastilleMapper pastilleMapper) {
        this.pastilleMapper = pastilleMapper;
    }

    public Manufacturer toEntity(ManufacturerRequestDTO dto) {

        Manufacturer manufacturer = new Manufacturer();

        manufacturer.setName(dto.name());
        manufacturer.setContact(dto.contact());
        manufacturer.setActive(dto.isActive());

        return manufacturer;
    }

    public ManufacturerResponseDTO toResponseDTO(Manufacturer manufacturer) {

        List<PastilleListDTO> pastilles = manufacturer.getPastilles()
                .stream()
                .map(pastilleMapper::toListDTO)
                .toList();

        return new ManufacturerResponseDTO(
                manufacturer.getId(),
                manufacturer.getName(),
                manufacturer.getContact(),
                manufacturer.getActive(),
                pastilles
        );
    }
}