package com.istock.system.mapper;

import com.istock.system.dtos.response.*;
import com.istock.system.entities.Manufacturer;
import com.istock.system.entities.Movement;
import com.istock.system.entities.Pastille;
import com.istock.system.entities.Supplier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PastilleMapper {

    public PastilleListDTO toListDTO(Pastille pastille) {
        return new PastilleListDTO(
                pastille.getId(),
                pastille.getSkuCode(),
                pastille.getDescription(),
                pastille.getMinimumStockLevel(),
                pastille.getCurrentBalance(),
                pastille.getManufacturer() != null
                        ? pastille.getManufacturer().getName()
                        : null
        );
    }

    public PastilleDTO toDTO(Pastille pastille) {
        return new PastilleDTO(
                pastille.getId(),
                pastille.getSkuCode(),
                pastille.getDescription(),
                new StockDTO(
                        pastille.getMinimumStockLevel(),
                        pastille.getCurrentBalance(),
                        pastille.onAlert()
                ),
                toManufacturerDTO(pastille.getManufacturer()),
                toSupplierListDTO(pastille.getSuppliers()),
                toMovementList(pastille.getMovements())
        );
    }

    private ManufacturerResponseDTO toManufacturerDTO(Manufacturer manufacturer) {
        if (manufacturer == null) {
            return null;
        }

        return new ManufacturerResponseDTO(
                manufacturer.getId(),
                manufacturer.getName(),
                manufacturer.getContact(),
                manufacturer.getActive(),
                toPastilleListDTO(manufacturer.getPastilles())
        );
    }

    private List<SupplierResponseDTO> toSupplierListDTO(List<Supplier> suppliers) {
        if (suppliers == null) {
            return List.of();
        }

        return suppliers.stream()
                .map(supplier -> new SupplierResponseDTO(
                        supplier.getId(),
                        supplier.getName(),
                        supplier.getContact(),
                        supplier.getActive(),
                        toPastilleListDTO(supplier.getPastilles())
                ))
                .toList();
    }

    private List<MovementDTO> toMovementList(List<Movement> movements) {
        if (movements == null) {
            return List.of();
        }

        return movements.stream()
                .map(movement -> new MovementDTO(
                        movement.getId(),
                        movement.getQuantity(),
                        movement.getDateTime(),
                        new ReasonMovementDTO(
                                movement.getReasonMovement().getId(),
                                movement.getReasonMovement().getDescription(),
                                movement.getReasonMovement().getType()
                        ),
                        new UserDTO(
                                movement.getUser().getId(),
                                movement.getUser().getName()
                        )
                ))
                .toList();
    }

    private List<PastilleListDTO> toPastilleListDTO(List<Pastille> pastilles) {
        if (pastilles == null) {
            return List.of();
        }

        return pastilles.stream()
                .map(this::toListDTO)
                .toList();
    }
}
