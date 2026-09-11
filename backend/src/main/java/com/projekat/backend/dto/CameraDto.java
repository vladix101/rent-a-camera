package com.projekat.backend.dto;

import java.time.LocalDate;

public class CameraDto {
    private Long id;
    private LocalDate purchaseDate;
    private String note;
    private Boolean available;
    private CategoryDto category;
    private ManufacturerDto manufacturer;
    private SpecificationDto specification;
    private Boolean availableForPeriod;

    public CameraDto() {
    }

    public CameraDto(Long id, LocalDate purchaseDate, String note, Boolean available,
                          CategoryDto category, ManufacturerDto manufacturer, SpecificationDto specification,
                          Boolean availableForPeriod) {
        this.id = id;
        this.purchaseDate = purchaseDate;
        this.note = note;
        this.available = available;
        this.category = category;
        this.manufacturer = manufacturer;
        this.specification = specification;
        this.availableForPeriod = availableForPeriod;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public CategoryDto getCategory() {
        return category;
    }

    public void setCategory(CategoryDto category) {
        this.category = category;
    }

    public ManufacturerDto getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(ManufacturerDto manufacturer) {
        this.manufacturer = manufacturer;
    }

    public SpecificationDto getSpecification() {
        return specification;
    }

    public void setSpecification(SpecificationDto specification) {
        this.specification = specification;
    }

    public Boolean getAvailableForPeriod() {
        return availableForPeriod;
    }

    public void setAvailableForPeriod(Boolean availableForPeriod) {
        this.availableForPeriod = availableForPeriod;
    }
}
