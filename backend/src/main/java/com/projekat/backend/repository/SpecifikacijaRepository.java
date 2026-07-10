package com.projekat.backend.repository;

import com.projekat.backend.entity.Specifikacija;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecifikacijaRepository extends JpaRepository<Specifikacija, Long> {
}
