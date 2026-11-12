package com.istock.system.mapper;

import com.istock.system.dtos.response.*;
import com.istock.system.entities.Manufacturer;
import com.istock.system.entities.Movement;
import com.istock.system.entities.Pastille;
import com.istock.system.entities.Supplier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
                pastille.getManufacturer().getName()
        );
    }

    public PastilleDTO toDTO(
            Pastille pastille
    ) {
        return new PastilleDTO(
                pastille.getId(),
                pastille.getSkuCode(),
                pastille.getDescription(),
                new StockDTO(
                        pastille.getMinimumStockLevel(),
                        pastille.getCurrentBalance(),
                        pastille.onAlert()
                ),
                this.toManufacturerDTO(pastille.getManufacturer()),
                this.toSupplierListDTO(pastille.getSuppliers()),
                this.toMovementList(pastille.getMovements())
        );
    }

    private ManufacturerDTO toManufacturerDTO(Manufacturer manufacturer) {
        return new ManufacturerDTO(
                manufacturer.getId(),
                manufacturer.getName(),
                manufacturer.getContact(),
                manufacturer.getActive()
        );
    }

    private List<SupplierDTO> toSupplierListDTO(List<Supplier> suppliers) {
        List<SupplierDTO> supplierDTOS = new ArrayList<>();

        for (Supplier supplier : suppliers) {
            supplierDTOS.add(new SupplierDTO(
                    supplier.getId(),
                    supplier.getName(),
                    supplier.getContact(),
                    supplier.getActive()
            ));
        }

        return supplierDTOS;
    }

    private List<MovementDTO> toMovementList(List<Movement> movements) {
        List<MovementDTO> movementDTOS = new ArrayList<>();

        for (Movement movement : movements) {
            UserDTO user = new UserDTO(
                    movement.getUser().getId(),
                    movement.getUser().getName()
            );

            ReasonMovementDTO reason = new ReasonMovementDTO(
                    movement.getReasonMovement().getId(),
                    movement.getReasonMovement().getDescription(),
                    movement.getReasonMovement().getType()
            );

            movementDTOS.add(new MovementDTO(
                    movement.getId(),
                    movement.getQuantity(),
                    movement.getDateTime(),
                    reason,
                    user
            ));
        }

        return movementDTOS;
    }
}
