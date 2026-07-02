package com.projekat.backend.repository;

import com.projekat.backend.entity.Iznajmljivanje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IznajmljivanjeRepository extends JpaRepository<Iznajmljivanje, Long> {
    List<Iznajmljivanje> findByDatumPocetkaLessThanEqualAndDatumKrajaGreaterThanEqual(LocalDate datumDo, LocalDate datumOd);
    List<Iznajmljivanje> findByKlijentId(Long klijentId);
    List<Iznajmljivanje> findByFotoaparatId(Long fotoaparatId);
    boolean existsByFotoaparatIdAndDatumPocetkaLessThanEqualAndDatumKrajaGreaterThanEqual(Long fotoaparatId, LocalDate datumDo, LocalDate datumOd);
}
