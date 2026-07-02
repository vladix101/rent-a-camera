package com.projekat.backend.dto;

import java.time.LocalDate;

public class FotoaparatRequestDto {
    private String proizvodjacNaziv;
    private Long kategorijaId;
    private LocalDate datumKupovine;
    private String napomena;
    private Boolean dostupan;
    private String rezolucija;
    private String senzorSlike;
    private Boolean wifi;
    private String ekran;
    private String napajanje;
    private String velicinaSlike;
    private String opis;

    public String getProizvodjacNaziv() {
        return proizvodjacNaziv;
    }

    public void setProizvodjacNaziv(String proizvodjacNaziv) {
        this.proizvodjacNaziv = proizvodjacNaziv;
    }

    public Long getKategorijaId() {
        return kategorijaId;
    }

    public void setKategorijaId(Long kategorijaId) {
        this.kategorijaId = kategorijaId;
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

    public String getRezolucija() {
        return rezolucija;
    }

    public void setRezolucija(String rezolucija) {
        this.rezolucija = rezolucija;
    }

    public String getSenzorSlike() {
        return senzorSlike;
    }

    public void setSenzorSlike(String senzorSlike) {
        this.senzorSlike = senzorSlike;
    }

    public Boolean getWifi() {
        return wifi;
    }

    public void setWifi(Boolean wifi) {
        this.wifi = wifi;
    }

    public String getEkran() {
        return ekran;
    }

    public void setEkran(String ekran) {
        this.ekran = ekran;
    }

    public String getNapajanje() {
        return napajanje;
    }

    public void setNapajanje(String napajanje) {
        this.napajanje = napajanje;
    }

    public String getVelicinaSlike() {
        return velicinaSlike;
    }

    public void setVelicinaSlike(String velicinaSlike) {
        this.velicinaSlike = velicinaSlike;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }
}
