package com.projekat.backend.repository;

import com.projekat.backend.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate dateTo, LocalDate dateFrom);
    List<Rental> findByClientId(Long clientId);
    List<Rental> findByCameraId(Long cameraId);
    boolean existsByCameraIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long cameraId, LocalDate dateTo, LocalDate dateFrom);
}
