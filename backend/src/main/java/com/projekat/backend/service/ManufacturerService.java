package com.projekat.backend.service;

import com.projekat.backend.dto.ManufacturerDto;
import com.projekat.backend.entity.Manufacturer;
import com.projekat.backend.repository.ManufacturerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManufacturerService {

    private final ManufacturerRepository manufacturerRepository;

    @Transactional(readOnly = true)
    public List<ManufacturerDto> getManufacturers() {
        return manufacturerRepository.findAll()
                .stream()
                .map(manufacturer -> new ManufacturerDto(manufacturer.getId(), manufacturer.getName()))
                .toList();
    }
}
