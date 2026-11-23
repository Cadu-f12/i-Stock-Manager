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

    private CategoryView categoryOf(Pastille pastille) {
        var category = pastille.getCategory();
        return category == null ? null : new CategoryView(category.getId(), category.getName());
    }

    public PastilleListDTO toListDTO(Pastille pastille) {
        return new PastilleListDTO(
                pastille.getId(),
                pastille.getSkuCode(),
                pastille.getDescription(),
                pastille.getMinimumStockLevel(),
                pastille.getCurrentBalance(),
                pastille.getManufacturer() != null
                        ? pastille.getManufacturer().getName()
                        : null,
                pastille.getSuppliers().stream().map(Supplier::getName).sorted().toList(),
                categoryOf(pastille)
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
                toMovementList(pastille.getMovements()),
                categoryOf(pastille)
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
                manufacturer.getPastilles().stream()
                        .map(this::toListDTO)
                        .toList()
        );
    }

    private List<SupplierResponseDTO> toSupplierListDTO(List<Supplier> suppliers) {
        return suppliers.stream()
                .map(supplier -> new SupplierResponseDTO(
                        supplier.getId(),
                        supplier.getName(),
                        supplier.getContact(),
                        supplier.getActive(),
                        supplier.getPastilles().stream()
                                .map(this::toListDTO)
                                .toList()
                ))
                .toList();
    }

    private List<MovementDTO> toMovementList(List<Movement> movements) {
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
}