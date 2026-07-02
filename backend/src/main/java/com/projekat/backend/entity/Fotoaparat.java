package com.projekat.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Fotoaparat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate datumKupovine;
    private String napomena;
    private Boolean dostupan;

    @ManyToOne
    @JoinColumn(name = "proizvodjac_id")
    private Proizvodjac proizvodjac;

    @OneToMany(mappedBy = "fotoaparat", cascade = CascadeType.ALL)
    private List<Specifikcija> specifikcije = new ArrayList<>();

    @OneToMany(mappedBy = "fotoaparat")
    private List<Iznajmljivanje> iznajmljivanja = new ArrayList<>();

    public Fotoaparat() {
    }
}
