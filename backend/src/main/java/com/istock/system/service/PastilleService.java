package com.istock.system.service;

import com.istock.system.dtos.response.PastilleDTO;
import com.istock.system.entities.Pastille;
import com.istock.system.repository.PastilleRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PastilleService {

    private final PastilleRepository pastilleRepository;

    public PastilleService(PastilleRepository pastilleRepository) {
        this.pastilleRepository = pastilleRepository;
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
}
