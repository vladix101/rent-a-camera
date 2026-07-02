package com.projekat.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Iznajmljivanje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate datumPocetka;
    private LocalDate datumKraja;
    private Double cena;
    private String napomena;

    @ManyToOne
    @JoinColumn(name = "fotoaparat_id")
    private Fotoaparat fotoaparat;

    @ManyToOne
    @JoinColumn(name = "klijent_id")
    private Klijent klijent;

    @ManyToOne
    @JoinColumn(name = "zaposleni_id")
    private Zaposleni zaposleni;

    public Iznajmljivanje() {
    }
}
