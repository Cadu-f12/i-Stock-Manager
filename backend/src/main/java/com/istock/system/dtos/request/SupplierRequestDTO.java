package com.istock.system.dtos.request;

import com.istock.system.dtos.response.PastilleDTO;
import com.istock.system.dtos.response.PastilleListDTO;
import com.istock.system.entities.Pastille;

import java.util.List;

public record SupplierRequestDTO (
        Long id,
        String name,
        String contact,
        Boolean isActive,
        List<PastilleListDTO> pastilles
) {}
