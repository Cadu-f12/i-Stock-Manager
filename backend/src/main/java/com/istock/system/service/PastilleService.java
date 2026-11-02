package com.istock.system.service;

import com.istock.system.dtos.request.RegistryPastilleRequest;
import com.istock.system.dtos.response.*;
import com.istock.system.entities.Manufacturer;
import com.istock.system.entities.Movement;
import com.istock.system.entities.Pastille;
import com.istock.system.entities.Supplier;
import com.istock.system.exception.ManufacturerNotFound;
import com.istock.system.exception.PastilleAlreadyExists;
import com.istock.system.exception.PastilleNotFound;
import com.istock.system.repository.ManufacturerRepository;
import com.istock.system.repository.PastilleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PastilleService {

    private final PastilleRepository pastilleRepository;
    private final ManufacturerRepository manufacturerRepository;

    public PastilleService(
            PastilleRepository pastilleRepository,
            ManufacturerRepository manufacturerRepository
    ) {
        this.pastilleRepository = pastilleRepository;
        this.manufacturerRepository = manufacturerRepository;
    }

    @Transactional(readOnly = true)
    public List<PastilleListDTO> findAll(Boolean onAlert) {
        List<Pastille> pastilles;

        if (Boolean.TRUE.equals(onAlert)) {
            pastilles = pastilleRepository.findAllOnAlert();
        } else {
            pastilles = pastilleRepository.findAll();
        }

        List<PastilleListDTO> pastilleList = new ArrayList<>();
        for (Pastille pastille : pastilles) {
            pastilleList.add(toListDTO(pastille));
        }

        return pastilleList;
    }

    @Transactional
    public PastilleListDTO registerPastille(RegistryPastilleRequest body) {
        if (pastilleRepository.existsBySkuCode(body.skuCode())) {
            throw new PastilleAlreadyExists(
                    "A pastille with SKU code '" + body.skuCode() + "' already exists."
            );
        }

        Manufacturer manufacturer = manufacturerRepository.findById(body.manufacturerId())
                .orElseThrow(() -> new ManufacturerNotFound(
                        "Manufacturer with ID '" + body.manufacturerId() + "' was not found."
                ));

        Pastille pastille = pastilleRepository.save(new Pastille(
                body.skuCode(),
                body.description(),
                body.minimumStockLevel(),
                0,
                manufacturer,
                new ArrayList<>(),
                new ArrayList<>()
        ));

        return toListDTO(pastille);
    }

    @Transactional(readOnly = true)
    public PastilleDTO findById(Long id) {
        Pastille pastille = pastilleRepository.findById(id)
                .orElseThrow(() -> new PastilleNotFound(
                        "Pastille with ID '" + id + "' was not found."
                ));
        List<SupplierDTO> supplierDTOS = new ArrayList<>();
        List<MovementDTO> movementDTOS = new ArrayList<>();

        ManufacturerDTO manufacturerDTO = new ManufacturerDTO(
                pastille.getManufacturer().getId(),
                pastille.getManufacturer().getName(),
                pastille.getManufacturer().getContact(),
                pastille.getManufacturer().getActive()
        );

        for (Supplier supplier : pastille.getSuppliers()) {
            supplierDTOS.add(new SupplierDTO(
                    supplier.getId(),
                    supplier.getName(),
                    supplier.getContact(),
                    supplier.getActive()
            ));
        }

        for (Movement movement : pastille.getMovements()) {
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

        return new PastilleDTO(
                pastille.getId(),
                pastille.getSkuCode(),
                pastille.getDescription(),
                new StockDTO(
                        pastille.getMinimumStockLevel(),
                        pastille.getCurrentBalance(),
                        pastille.getCurrentBalance() <= pastille.getMinimumStockLevel() + 5
                ),
                manufacturerDTO,
                supplierDTOS,
                movementDTOS
        );
    }

    private PastilleListDTO toListDTO(Pastille pastille) {
        return new PastilleListDTO(
                pastille.getId(),
                pastille.getSkuCode(),
                pastille.getDescription(),
                pastille.getMinimumStockLevel(),
                pastille.getCurrentBalance(),
                pastille.getManufacturer().getName()
        );
    }
}
