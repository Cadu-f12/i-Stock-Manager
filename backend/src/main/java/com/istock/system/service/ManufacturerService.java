package com.istock.system.service;

import com.istock.system.dtos.request.ManufacturerRequestDTO;
import com.istock.system.dtos.request.UserRequestDTO;
import com.istock.system.dtos.response.ManufacturerResponseDTO;
import com.istock.system.dtos.response.UserResponseDTO;
import com.istock.system.entities.Manufacturer;
import com.istock.system.entities.User;
import com.istock.system.exception.UserAlreadyExist;
import com.istock.system.exception.UserNotFound;
import com.istock.system.exception.UserNotFoundWithEmail;
import com.istock.system.mapper.ManufacturerMapper;
import com.istock.system.mapper.UserMapper;
import com.istock.system.repository.ManufacturerRepository;
import com.istock.system.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ManufacturerService {
    private final ManufacturerRepository manufacturerRepository;
    private final ManufacturerMapper manufacturerMapper;

    public ManufacturerService(ManufacturerRepository manufacturerRepository, ManufacturerMapper manufacturerMapper) {
        this.manufacturerRepository = manufacturerRepository;
        this.manufacturerMapper = manufacturerMapper;
    }

    public ManufacturerResponseDTO getManufacturer(Long id) {
        Manufacturer manufacturer = manufacturerRepository.findById(id) .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Manufacturer not found"
        ));
        return manufacturerMapper.toResponseDTO(manufacturer);
    }

    public List<ManufacturerResponseDTO> getAllManufacturers() {
        return manufacturerRepository.findAll()
                .stream()
                .map(manufacturerMapper::toResponseDTO)
                .toList();
    }

    public ManufacturerResponseDTO createManufacturer(ManufacturerRequestDTO dto) {

        Manufacturer manufacturer = manufacturerMapper.toEntity(dto);

//        if (manufacturerRepository.findByEmail(user.getEmail()).isPresent()) {
//            throw new UserAlreadyExist(user);
//        }


        manufacturer.setActive(true);

        Manufacturer savedManufacturer = manufacturerRepository.save(manufacturer);

        return manufacturerMapper.toResponseDTO(savedManufacturer);
    }

    public ManufacturerResponseDTO fetchManufacturer(String name) {
        Manufacturer manufacturer = manufacturerRepository.findByName(name)
                .orElseThrow(() -> new UserNotFoundWithEmail(name));
        return manufacturerMapper.toResponseDTO(manufacturer);
    }

    public ManufacturerResponseDTO updateManufacturer(Long id, ManufacturerRequestDTO dto) {

        Manufacturer manufacturer = manufacturerRepository.findById(id)
                .orElseThrow(() -> new UserNotFound(id));

        manufacturer.setPastilles(dto.pastilles());
        manufacturer.setName(dto.name());
        manufacturer.setContact(dto.contact());

        Manufacturer savedManufacturer = manufacturerRepository.save(manufacturer);

        return manufacturerMapper.toResponseDTO(savedManufacturer);
    }

    public ManufacturerResponseDTO deleteManufacturer(Long id) {

        Manufacturer manufacturer = manufacturerRepository.findById(id)
                .orElseThrow(() -> new UserNotFound(id));

        ManufacturerResponseDTO response = manufacturerMapper.toResponseDTO(manufacturer);

        manufacturerRepository.delete(manufacturer);

        return response;
    }
}
