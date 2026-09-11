package com.projekat.backend.dto;

public class SpecificationDto {
    private Long id;
    private String resolution;
    private String imageSensor;
    private Boolean wifi;
    private String screen;
    private String power;
    private String imageSize;
    private String description;

    public SpecificationDto() {
    }

    public SpecificationDto(Long id, String resolution, String imageSensor, Boolean wifi, String screen,
                             String power, String imageSize, String description) {
        this.id = id;
        this.resolution = resolution;
        this.imageSensor = imageSensor;
        this.wifi = wifi;
        this.screen = screen;
        this.power = power;
        this.imageSize = imageSize;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getImageSensor() {
        return imageSensor;
    }

    public void setImageSensor(String imageSensor) {
        this.imageSensor = imageSensor;
    }

    public Boolean getWifi() {
        return wifi;
    }

    public void setWifi(Boolean wifi) {
        this.wifi = wifi;
    }

    public String getScreen() {
        return screen;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getImageSize() {
        return imageSize;
    }

    public void setImageSize(String imageSize) {
        this.imageSize = imageSize;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
