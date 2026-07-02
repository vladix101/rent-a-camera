package com.projekat.backend.repository;

import com.projekat.backend.entity.Kategorija;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KategorijaRepository extends JpaRepository<Kategorija, Long> {
}
