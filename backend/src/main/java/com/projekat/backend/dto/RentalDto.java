package com.projekat.backend.dto;

import java.time.LocalDate;

public class RentalDto {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double price;
    private Long cameraId;
    private String manufacturerName;
    private String categoryName;
    private String resolution;
    private String description;

    public RentalDto() {
    }

    public RentalDto(Long id, LocalDate startDate, LocalDate endDate, Double price, Long cameraId,
                              String manufacturerName, String categoryName, String resolution, String description) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.cameraId = cameraId;
        this.manufacturerName = manufacturerName;
        this.categoryName = categoryName;
        this.resolution = resolution;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getCameraId() {
        return cameraId;
    }

    public void setCameraId(Long cameraId) {
        this.cameraId = cameraId;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
