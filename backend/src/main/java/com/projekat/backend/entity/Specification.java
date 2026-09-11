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
public class Specification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String resolution;
    private String imageSensor;
    private Boolean wifi;
    private String screen;
    private String power;
    private String imageSize;
    @Column(length = 1000)
    private String description;

    @OneToMany(mappedBy = "specification")
    private List<Camera> cameras = new ArrayList<>();

    public Specification() {
    }
}
