package com.projekat.backend.entity;

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
public class Klijent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ime;
    private String prezime;
    private Integer starost;
    private String username;
    private String password;
    private String email;

    @OneToMany(mappedBy = "klijent")
    private List<Iznajmljivanje> iznajmljivanja = new ArrayList<>();

    public Klijent() {
    }
}
