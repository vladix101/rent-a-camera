package com.projekat.backend.dto;

import java.time.LocalDate;

public class FotoaparatDto {
    private Long id;
    private LocalDate datumKupovine;
    private String napomena;
    private Boolean dostupan;
    private String proizvodjacNaziv;
    private String kategorijaNaziv;
    private String rezolucija;
    private String senzorSlike;
    private Boolean wifi;
    private String ekran;
    private String napajanje;
    private String velicinaSlike;
    private String opis;
    private Boolean dostupanZaPeriod;

    public FotoaparatDto() {
    }

    public FotoaparatDto(Long id, LocalDate datumKupovine, String napomena, Boolean dostupan,
                          String proizvodjacNaziv, String kategorijaNaziv, String rezolucija, String senzorSlike,
                          Boolean wifi, String ekran, String napajanje, String velicinaSlike, String opis,
                          Boolean dostupanZaPeriod) {
        this.id = id;
        this.datumKupovine = datumKupovine;
        this.napomena = napomena;
        this.dostupan = dostupan;
        this.proizvodjacNaziv = proizvodjacNaziv;
        this.kategorijaNaziv = kategorijaNaziv;
        this.rezolucija = rezolucija;
        this.senzorSlike = senzorSlike;
        this.wifi = wifi;
        this.ekran = ekran;
        this.napajanje = napajanje;
        this.velicinaSlike = velicinaSlike;
        this.opis = opis;
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

    public String getProizvodjacNaziv() {
        return proizvodjacNaziv;
    }

    public void setProizvodjacNaziv(String proizvodjacNaziv) {
        this.proizvodjacNaziv = proizvodjacNaziv;
    }

    public String getKategorijaNaziv() {
        return kategorijaNaziv;
    }

    public void setKategorijaNaziv(String kategorijaNaziv) {
        this.kategorijaNaziv = kategorijaNaziv;
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

    public Boolean getDostupanZaPeriod() {
        return dostupanZaPeriod;
    }

    public void setDostupanZaPeriod(Boolean dostupanZaPeriod) {
        this.dostupanZaPeriod = dostupanZaPeriod;
    }
}
