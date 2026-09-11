package com.projekat.backend.dto;

import java.time.LocalDate;

public class OccupancyDto {
    private LocalDate dateFrom;
    private LocalDate dateTo;

    public OccupancyDto() {
    }

    public OccupancyDto(LocalDate dateFrom, LocalDate dateTo) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }
}
