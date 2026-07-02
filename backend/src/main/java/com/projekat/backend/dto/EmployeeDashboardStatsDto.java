package com.projekat.backend.dto;

import java.util.List;

public class EmployeeDashboardStatsDto {
    private Long ukupanBrojIznajmljivanja;
    private Long ukupanBrojKlijenata;
    private Long ukupanBrojFotoaparata;
    private Long brojDostupnihFotoaparata;
    private Long brojNedostupnihFotoaparata;
    private String najpopularnijiFotoaparat;
    private List<RentalStatsDto> statistikaPoFotoaparatu;

    public EmployeeDashboardStatsDto() {
    }

    public EmployeeDashboardStatsDto(Long ukupanBrojIznajmljivanja, Long ukupanBrojKlijenata, Long ukupanBrojFotoaparata,
                                      Long brojDostupnihFotoaparata, Long brojNedostupnihFotoaparata,
                                      String najpopularnijiFotoaparat, List<RentalStatsDto> statistikaPoFotoaparatu) {
        this.ukupanBrojIznajmljivanja = ukupanBrojIznajmljivanja;
        this.ukupanBrojKlijenata = ukupanBrojKlijenata;
        this.ukupanBrojFotoaparata = ukupanBrojFotoaparata;
        this.brojDostupnihFotoaparata = brojDostupnihFotoaparata;
        this.brojNedostupnihFotoaparata = brojNedostupnihFotoaparata;
        this.najpopularnijiFotoaparat = najpopularnijiFotoaparat;
        this.statistikaPoFotoaparatu = statistikaPoFotoaparatu;
    }

    public Long getUkupanBrojIznajmljivanja() {
        return ukupanBrojIznajmljivanja;
    }

    public void setUkupanBrojIznajmljivanja(Long ukupanBrojIznajmljivanja) {
        this.ukupanBrojIznajmljivanja = ukupanBrojIznajmljivanja;
    }

    public Long getUkupanBrojKlijenata() {
        return ukupanBrojKlijenata;
    }

    public void setUkupanBrojKlijenata(Long ukupanBrojKlijenata) {
        this.ukupanBrojKlijenata = ukupanBrojKlijenata;
    }

    public Long getUkupanBrojFotoaparata() {
        return ukupanBrojFotoaparata;
    }

    public void setUkupanBrojFotoaparata(Long ukupanBrojFotoaparata) {
        this.ukupanBrojFotoaparata = ukupanBrojFotoaparata;
    }

    public Long getBrojDostupnihFotoaparata() {
        return brojDostupnihFotoaparata;
    }

    public void setBrojDostupnihFotoaparata(Long brojDostupnihFotoaparata) {
        this.brojDostupnihFotoaparata = brojDostupnihFotoaparata;
    }

    public Long getBrojNedostupnihFotoaparata() {
        return brojNedostupnihFotoaparata;
    }

    public void setBrojNedostupnihFotoaparata(Long brojNedostupnihFotoaparata) {
        this.brojNedostupnihFotoaparata = brojNedostupnihFotoaparata;
    }

    public String getNajpopularnijiFotoaparat() {
        return najpopularnijiFotoaparat;
    }

    public void setNajpopularnijiFotoaparat(String najpopularnijiFotoaparat) {
        this.najpopularnijiFotoaparat = najpopularnijiFotoaparat;
    }

    public List<RentalStatsDto> getStatistikaPoFotoaparatu() {
        return statistikaPoFotoaparatu;
    }

    public void setStatistikaPoFotoaparatu(List<RentalStatsDto> statistikaPoFotoaparatu) {
        this.statistikaPoFotoaparatu = statistikaPoFotoaparatu;
    }
}
