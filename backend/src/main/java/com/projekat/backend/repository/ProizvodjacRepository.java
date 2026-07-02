package com.projekat.backend.repository;

import com.projekat.backend.entity.Proizvodjac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProizvodjacRepository extends JpaRepository<Proizvodjac, Long> {
    Optional<Proizvodjac> findByNameIgnoreCase(String name);
}
