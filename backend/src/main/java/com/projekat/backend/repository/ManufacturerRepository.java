package com.projekat.backend.repository;

import com.projekat.backend.entity.Manufacturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {
    Optional<Manufacturer> findByNameIgnoreCase(String name);
}
