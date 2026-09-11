package com.projekat.backend.service;

import com.projekat.backend.dto.CameraDto;
import com.projekat.backend.dto.CameraRequestDto;
import com.projekat.backend.dto.CategoryDto;
import com.projekat.backend.dto.ManufacturerDto;
import com.projekat.backend.dto.SpecificationDto;
import com.projekat.backend.dto.OccupancyDto;
import com.projekat.backend.entity.Camera;
import com.projekat.backend.entity.Rental;
import com.projekat.backend.entity.Category;
import com.projekat.backend.entity.Manufacturer;
import com.projekat.backend.entity.Specification;
import com.projekat.backend.exception.ValidationException;
import com.projekat.backend.repository.CameraRepository;
import com.projekat.backend.repository.RentalRepository;
import com.projekat.backend.repository.CategoryRepository;
import com.projekat.backend.repository.ManufacturerRepository;
import com.projekat.backend.repository.SpecificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CameraService {

    private final CameraRepository cameraRepository;
    private final RentalRepository rentalRepository;
    private final CategoryRepository categoryRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final SpecificationRepository specificationRepository;

    @Transactional(readOnly = true)
    public List<CameraDto> getCameras(LocalDate dateFrom, LocalDate dateTo) {
        LocalDate periodOd = dateFrom != null ? dateFrom : LocalDate.now();
        LocalDate periodDo = dateTo != null ? dateTo : periodOd.plusDays(1);

        Set<Long> occupiedCameraIds = rentalRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(periodDo, periodOd)
                .stream()
                .map(Rental::getCamera)
                .map(Camera::getId)
                .collect(Collectors.toSet());

        return cameraRepository.findAll()
                .stream()
                .map(camera -> toDto(camera, occupiedCameraIds))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OccupancyDto> getOccupiedPeriods(Long cameraId) {
        if (!cameraRepository.existsById(cameraId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Camera not found");
        }

        return rentalRepository.findByCameraId(cameraId)
                .stream()
                .map(rental -> new OccupancyDto(rental.getStartDate(), rental.getEndDate()))
                .toList();
    }

    @Transactional
    public CameraDto createCamera(CameraRequestDto requestDto) {
        validateCamera(requestDto);

        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        Manufacturer manufacturer = manufacturerRepository.findById(requestDto.getManufacturerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Manufacturer not found"));
        Specification specification = specificationRepository.findById(requestDto.getSpecificationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Specification not found"));

        Camera camera = new Camera();
        applyFields(camera, requestDto, category, manufacturer, specification);

        camera = cameraRepository.save(camera);
        return toDto(camera, Set.of());
    }

    @Transactional
    public CameraDto updateCamera(Long id, CameraRequestDto requestDto) {
        validateCamera(requestDto);

        Camera camera = cameraRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Camera not found"));
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        Manufacturer manufacturer = manufacturerRepository.findById(requestDto.getManufacturerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Manufacturer not found"));
        Specification specification = specificationRepository.findById(requestDto.getSpecificationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Specification not found"));

        applyFields(camera, requestDto, category, manufacturer, specification);

        camera = cameraRepository.save(camera);
        return toDto(camera, Set.of());
    }

    @Transactional
    public void deleteCamera(Long id) {
        Camera camera = cameraRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Camera not found"));

        boolean hasRentals = !rentalRepository.findByCameraId(id).isEmpty();
        if (hasRentals) {
            camera.setAvailable(false);
            cameraRepository.save(camera);
        } else {
            cameraRepository.delete(camera);
        }
    }

    private void applyFields(Camera camera, CameraRequestDto requestDto, Category category,
                              Manufacturer manufacturer, Specification specification) {
        camera.setPurchaseDate(requestDto.getPurchaseDate());
        camera.setNote(requestDto.getNote());
        camera.setAvailable(requestDto.getAvailable() != null ? requestDto.getAvailable() : true);
        camera.setCategory(category);
        camera.setManufacturer(manufacturer);
        camera.setSpecification(specification);
    }

    private void validateCamera(CameraRequestDto requestDto) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        if (requestDto.getManufacturerId() == null) {
            fieldErrors.put("manufacturerId", "Manufacturer is required");
        }
        if (requestDto.getCategoryId() == null) {
            fieldErrors.put("categoryId", "Category is required");
        }
        if (requestDto.getSpecificationId() == null) {
            fieldErrors.put("specificationId", "Specification is required");
        }

        if (!fieldErrors.isEmpty()) {
            throw new ValidationException(fieldErrors);
        }
    }

    private CameraDto toDto(Camera camera, Set<Long> occupiedCameraIds) {
        Specification specification = camera.getSpecification();
        Category category = camera.getCategory();
        Manufacturer manufacturer = camera.getManufacturer();
        boolean availableForPeriod = Boolean.TRUE.equals(camera.getAvailable()) && !occupiedCameraIds.contains(camera.getId());

        CategoryDto categoryDto = category == null ? null : new CategoryDto(category.getId(), category.getName());
        ManufacturerDto manufacturerDto = manufacturer == null ? null : new ManufacturerDto(manufacturer.getId(), manufacturer.getName());
        SpecificationDto specificationDto = specification == null ? null : new SpecificationDto(
                specification.getId(),
                specification.getResolution(),
                specification.getImageSensor(),
                specification.getWifi(),
                specification.getScreen(),
                specification.getPower(),
                specification.getImageSize(),
                specification.getDescription()
        );

        return new CameraDto(
                camera.getId(),
                camera.getPurchaseDate(),
                camera.getNote(),
                camera.getAvailable(),
                categoryDto,
                manufacturerDto,
                specificationDto,
                availableForPeriod
        );
    }
}
