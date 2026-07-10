package com.projekat.backend.dto;

public class SpecifikacijaDto {
    private Long id;
    private String rezolucija;
    private String senzorSlike;
    private Boolean wifi;
    private String ekran;
    private String napajanje;
    private String velicinaSlike;
    private String opis;

    public SpecifikacijaDto() {
    }

    public SpecifikacijaDto(Long id, String rezolucija, String senzorSlike, Boolean wifi, String ekran,
                             String napajanje, String velicinaSlike, String opis) {
        this.id = id;
        this.rezolucija = rezolucija;
        this.senzorSlike = senzorSlike;
        this.wifi = wifi;
        this.ekran = ekran;
        this.napajanje = napajanje;
        this.velicinaSlike = velicinaSlike;
        this.opis = opis;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
