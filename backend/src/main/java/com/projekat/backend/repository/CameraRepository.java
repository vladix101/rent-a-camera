package com.projekat.backend.repository;

import com.projekat.backend.entity.Camera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CameraRepository extends JpaRepository<Camera, Long> {
    long countByAvailableTrue();
    long countByAvailableFalse();
}
