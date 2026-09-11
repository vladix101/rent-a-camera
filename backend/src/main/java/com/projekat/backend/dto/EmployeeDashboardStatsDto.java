package com.projekat.backend.dto;

import java.util.List;

public class EmployeeDashboardStatsDto {
    private Long totalRentals;
    private Long totalClients;
    private Long totalCameras;
    private Long availableCameras;
    private Long unavailableCameras;
    private String mostPopularCamera;
    private List<RentalStatsDto> statsPerCamera;

    public EmployeeDashboardStatsDto() {
    }

    public EmployeeDashboardStatsDto(Long totalRentals, Long totalClients, Long totalCameras,
                                      Long availableCameras, Long unavailableCameras,
                                      String mostPopularCamera, List<RentalStatsDto> statsPerCamera) {
        this.totalRentals = totalRentals;
        this.totalClients = totalClients;
        this.totalCameras = totalCameras;
        this.availableCameras = availableCameras;
        this.unavailableCameras = unavailableCameras;
        this.mostPopularCamera = mostPopularCamera;
        this.statsPerCamera = statsPerCamera;
    }

    public Long getTotalRentals() {
        return totalRentals;
    }

    public void setTotalRentals(Long totalRentals) {
        this.totalRentals = totalRentals;
    }

    public Long getTotalClients() {
        return totalClients;
    }

    public void setTotalClients(Long totalClients) {
        this.totalClients = totalClients;
    }

    public Long getTotalCameras() {
        return totalCameras;
    }

    public void setTotalCameras(Long totalCameras) {
        this.totalCameras = totalCameras;
    }

    public Long getAvailableCameras() {
        return availableCameras;
    }

    public void setAvailableCameras(Long availableCameras) {
        this.availableCameras = availableCameras;
    }

    public Long getUnavailableCameras() {
        return unavailableCameras;
    }

    public void setUnavailableCameras(Long unavailableCameras) {
        this.unavailableCameras = unavailableCameras;
    }

    public String getMostPopularCamera() {
        return mostPopularCamera;
    }

    public void setMostPopularCamera(String mostPopularCamera) {
        this.mostPopularCamera = mostPopularCamera;
    }

    public List<RentalStatsDto> getStatsPerCamera() {
        return statsPerCamera;
    }

    public void setStatsPerCamera(List<RentalStatsDto> statsPerCamera) {
        this.statsPerCamera = statsPerCamera;
    }
}
