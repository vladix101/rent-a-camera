package com.projekat.backend.dto;

public class RentalStatsDto {
    private String cameraName;
    private Long rentalCount;
    private Double percentage;

    public RentalStatsDto() {
    }

    public RentalStatsDto(String cameraName, Long rentalCount, Double percentage) {
        this.cameraName = cameraName;
        this.rentalCount = rentalCount;
        this.percentage = percentage;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public Long getRentalCount() {
        return rentalCount;
    }

    public void setRentalCount(Long rentalCount) {
        this.rentalCount = rentalCount;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }
}
