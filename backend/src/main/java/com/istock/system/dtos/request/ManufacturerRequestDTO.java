package com.istock.system.dtos.request;

import com.istock.system.entities.Pastille;

import java.util.List;

public record ManufacturerRequestDTO(
        Long id,
        List<Pastille> pastilles,
        String name,
        String contact,
        Boolean isActive
) {}