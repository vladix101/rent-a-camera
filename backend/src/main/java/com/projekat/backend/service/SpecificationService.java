package com.projekat.backend.service;

import com.projekat.backend.dto.SpecificationDto;
import com.projekat.backend.entity.Specification;
import com.projekat.backend.exception.ValidationException;
import com.projekat.backend.repository.SpecificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SpecificationService {

    private final SpecificationRepository specificationRepository;

    @Transactional(readOnly = true)
    public List<SpecificationDto> getSpecifications() {
        return specificationRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public SpecificationDto createSpecification(SpecificationDto requestDto) {
        validateSpecification(requestDto);

        Specification specification = new Specification();
        specification.setResolution(requestDto.getResolution());
        specification.setImageSensor(requestDto.getImageSensor());
        specification.setWifi(requestDto.getWifi() != null && requestDto.getWifi());
        specification.setScreen(requestDto.getScreen());
        specification.setPower(requestDto.getPower());
        specification.setImageSize(requestDto.getImageSize());
        specification.setDescription(requestDto.getDescription());

        specification = specificationRepository.save(specification);
        return toDto(specification);
    }

    private void validateSpecification(SpecificationDto requestDto) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        if (requestDto.getResolution() == null || requestDto.getResolution().isBlank()) {
            fieldErrors.put("resolution", "Resolution is required");
        }
        if (requestDto.getImageSensor() == null || requestDto.getImageSensor().isBlank()) {
            fieldErrors.put("imageSensor", "Image sensor is required");
        }

        if (!fieldErrors.isEmpty()) {
            throw new ValidationException(fieldErrors);
        }
    }

    private SpecificationDto toDto(Specification specification) {
        return new SpecificationDto(
                specification.getId(),
                specification.getResolution(),
                specification.getImageSensor(),
                specification.getWifi(),
                specification.getScreen(),
                specification.getPower(),
                specification.getImageSize(),
                specification.getDescription()
        );
    }
}
