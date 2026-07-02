package com.projekat.backend.repository;

import com.projekat.backend.entity.Zaposleni;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ZaposleniRepository extends JpaRepository<Zaposleni, Long> {
    Optional<Zaposleni> findByUsername(String username);
}
