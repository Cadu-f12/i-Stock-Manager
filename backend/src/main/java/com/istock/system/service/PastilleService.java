package com.istock.system.service;

import com.istock.system.dtos.request.RegistryPastilleRequest;
import com.istock.system.dtos.response.*;
import com.istock.system.entities.Manufacturer;
import com.istock.system.entities.Pastille;
import com.istock.system.exception.ManufacturerNotFound;
import com.istock.system.exception.PastilleAlreadyExists;
import com.istock.system.exception.PastilleNotFound;
import com.istock.system.mapper.PastilleMapper;
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
    private final PastilleMapper mapper;

    public PastilleService(
            PastilleRepository pastilleRepository,
            ManufacturerRepository manufacturerRepository,
            PastilleMapper mapper
    ) {
        this.pastilleRepository = pastilleRepository;
        this.manufacturerRepository = manufacturerRepository;
        this.mapper = mapper;
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
            pastilleList.add(mapper.toListDTO(pastille));
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

        return mapper.toListDTO(pastille);
    }

    @Transactional(readOnly = true)
    public PastilleDTO findById(Long id) {
        Pastille pastille = pastilleRepository.findById(id)
                .orElseThrow(() -> new PastilleNotFound(
                        "Pastille with ID '" + id + "' was not found."
                ));

        return mapper.toDTO(pastille);
    }

}
