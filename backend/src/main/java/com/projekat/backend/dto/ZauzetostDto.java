package com.projekat.backend.dto;

import java.time.LocalDate;

public class ZauzetostDto {
    private LocalDate datumOd;
    private LocalDate datumDo;

    public ZauzetostDto() {
    }

    public ZauzetostDto(LocalDate datumOd, LocalDate datumDo) {
        this.datumOd = datumOd;
        this.datumDo = datumDo;
    }

    public LocalDate getDatumOd() {
        return datumOd;
    }

    public void setDatumOd(LocalDate datumOd) {
        this.datumOd = datumOd;
    }

    public LocalDate getDatumDo() {
        return datumDo;
    }

    public void setDatumDo(LocalDate datumDo) {
        this.datumDo = datumDo;
    }
}
