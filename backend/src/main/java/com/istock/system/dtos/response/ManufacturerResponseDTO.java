    package com.istock.system.dtos.response;

    import com.istock.system.entities.Pastille;

    import java.util.List;

    public record ManufacturerResponseDTO(
            Long id,
            String name,
            String contact,
            Boolean isActive,
            List<Pastille> pastilles
    ) {}