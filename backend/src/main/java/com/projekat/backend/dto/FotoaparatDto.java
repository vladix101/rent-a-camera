package com.projekat.backend.dto;

import java.time.LocalDate;

public class FotoaparatDto {
    private Long id;
    private LocalDate datumKupovine;
    private String napomena;
    private Boolean dostupan;
    private KategorijaDto kategorija;
    private ProizvodjacDto proizvodjac;
    private SpecifikacijaDto specifikacija;
    private Boolean dostupanZaPeriod;

    public FotoaparatDto() {
    }

    public FotoaparatDto(Long id, LocalDate datumKupovine, String napomena, Boolean dostupan,
                          KategorijaDto kategorija, ProizvodjacDto proizvodjac, SpecifikacijaDto specifikacija,
                          Boolean dostupanZaPeriod) {
        this.id = id;
        this.datumKupovine = datumKupovine;
        this.napomena = napomena;
        this.dostupan = dostupan;
        this.kategorija = kategorija;
        this.proizvodjac = proizvodjac;
        this.specifikacija = specifikacija;
        this.dostupanZaPeriod = dostupanZaPeriod;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public KategorijaDto getKategorija() {
        return kategorija;
    }

    public void setKategorija(KategorijaDto kategorija) {
        this.kategorija = kategorija;
    }

    public ProizvodjacDto getProizvodjac() {
        return proizvodjac;
    }

    public void setProizvodjac(ProizvodjacDto proizvodjac) {
        this.proizvodjac = proizvodjac;
    }

    public SpecifikacijaDto getSpecifikacija() {
        return specifikacija;
    }

    public void setSpecifikacija(SpecifikacijaDto specifikacija) {
        this.specifikacija = specifikacija;
    }

    public Boolean getDostupanZaPeriod() {
        return dostupanZaPeriod;
    }

    public void setDostupanZaPeriod(Boolean dostupanZaPeriod) {
        this.dostupanZaPeriod = dostupanZaPeriod;
    }
}
