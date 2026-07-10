package com.projekat.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Specifikacija {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String rezolucija;
    private String senzorSlike;
    private Boolean wifi;
    private String ekran;
    private String napajanje;
    private String velicinaSlike;
    @Column(length = 1000)
    private String opis;

    @OneToMany(mappedBy = "specifikacija")
    private List<Fotoaparat> fotoaparati = new ArrayList<>();

    public Specifikacija() {
    }
}
