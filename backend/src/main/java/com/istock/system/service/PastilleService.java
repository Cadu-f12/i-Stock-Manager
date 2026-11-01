package com.istock.system.service;

import com.istock.system.dtos.request.RegistryPastilleRequest;
import com.istock.system.dtos.response.PastilleDTO;
import com.istock.system.entities.Manufacturer;
import com.istock.system.entities.Pastille;
import com.istock.system.exception.ManufacturerNotFound;
import com.istock.system.exception.PastilleAlreadyExists;
import com.istock.system.repository.ManufacturerRepository;
import com.istock.system.repository.PastilleRepository;
import org.springframework.stereotype.Service;

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

    public List<PastilleDTO> findAll(Boolean onAlert) {
        List<Pastille> pastilles;

        if (Boolean.TRUE.equals(onAlert)) {
            pastilles = pastilleRepository.findAllOnAlert();
        } else {
            pastilles = pastilleRepository.findAll();
        }

        List<PastilleDTO> pastilleDTOS = new ArrayList<>();
        for (Pastille pastille : pastilles) {
            pastilleDTOS.add(new PastilleDTO(
               pastille.getId(),
               pastille.getSkuCode(),
               pastille.getDescription(),
               pastille.getMinimumStockLevel(),
               pastille.getCurrentBalance(),
               pastille.getManufacturer().getName()
            ));
        }

        return pastilleDTOS;
    }

    public PastilleDTO registerPastille(RegistryPastilleRequest body) {
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

        return new PastilleDTO(
                pastille.getId(),
                pastille.getSkuCode(),
                pastille.getDescription(),
                pastille.getMinimumStockLevel(),
                pastille.getCurrentBalance(),
                pastille.getManufacturer().getName()
        );
    }
}
