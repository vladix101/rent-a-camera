package com.projekat.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Specifikcija {
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

    @ManyToOne
    @JoinColumn(name = "kategorija_id")
    private Kategorija kategorija;

    @ManyToOne
    @JoinColumn(name = "fotoaparat_id")
    private Fotoaparat fotoaparat;

    public Specifikcija() {
    }
}
