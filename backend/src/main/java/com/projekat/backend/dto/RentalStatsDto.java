package com.projekat.backend.dto;

public class RentalStatsDto {
    private String nazivFotoaparata;
    private Long brojIznajmljivanja;
    private Double procenat;

    public RentalStatsDto() {
    }

    public RentalStatsDto(String nazivFotoaparata, Long brojIznajmljivanja, Double procenat) {
        this.nazivFotoaparata = nazivFotoaparata;
        this.brojIznajmljivanja = brojIznajmljivanja;
        this.procenat = procenat;
    }

    public String getNazivFotoaparata() {
        return nazivFotoaparata;
    }

    public void setNazivFotoaparata(String nazivFotoaparata) {
        this.nazivFotoaparata = nazivFotoaparata;
    }

    public Long getBrojIznajmljivanja() {
        return brojIznajmljivanja;
    }

    public void setBrojIznajmljivanja(Long brojIznajmljivanja) {
        this.brojIznajmljivanja = brojIznajmljivanja;
    }

    public Double getProcenat() {
        return procenat;
    }

    public void setProcenat(Double procenat) {
        this.procenat = procenat;
    }
}
