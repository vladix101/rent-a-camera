package com.projekat.backend.repository;

import com.projekat.backend.entity.Klijent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KlijentRepository extends JpaRepository<Klijent, Long> {
    Optional<Klijent> findByUsername(String username);
    boolean existsByUsername(String username);
}
