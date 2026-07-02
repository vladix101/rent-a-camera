package com.projekat.backend.repository;

import com.projekat.backend.entity.Fotoaparat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FotoaparatRepository extends JpaRepository<Fotoaparat, Long> {
}
