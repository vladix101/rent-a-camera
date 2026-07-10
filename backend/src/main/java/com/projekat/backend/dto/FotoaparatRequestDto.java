package com.projekat.backend.dto;

import java.time.LocalDate;

public class FotoaparatRequestDto {
    private Long proizvodjacId;
    private Long kategorijaId;
    private Long specifikacijaId;
    private LocalDate datumKupovine;
    private String napomena;
    private Boolean dostupan;

    public Long getProizvodjacId() {
        return proizvodjacId;
    }

    public void setProizvodjacId(Long proizvodjacId) {
        this.proizvodjacId = proizvodjacId;
    }

    public Long getKategorijaId() {
        return kategorijaId;
    }

    public void setKategorijaId(Long kategorijaId) {
        this.kategorijaId = kategorijaId;
    }

    public Long getSpecifikacijaId() {
        return specifikacijaId;
    }

    public void setSpecifikacijaId(Long specifikacijaId) {
        this.specifikacijaId = specifikacijaId;
    }

    public LocalDate getDatumKupovine() {
        return datumKupovine;
    }

    public void setDatumKupovine(LocalDate datumKupovine) {
        this.datumKupovine = datumKupovine;
    }

    public String getNapomena() {
        return napomena;
    }

    public void setNapomena(String napomena) {
        this.napomena = napomena;
    }

    public Boolean getDostupan() {
        return dostupan;
    }

    public void setDostupan(Boolean dostupan) {
        this.dostupan = dostupan;
    }
}
