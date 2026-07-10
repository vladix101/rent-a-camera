package com.projekat.backend.service;

import com.projekat.backend.dto.ProizvodjacDto;
import com.projekat.backend.entity.Proizvodjac;
import com.projekat.backend.repository.ProizvodjacRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProizvodjacService {

    private final ProizvodjacRepository proizvodjacRepository;

    @Transactional(readOnly = true)
    public List<ProizvodjacDto> getProizvodjaci() {
        return proizvodjacRepository.findAll()
                .stream()
                .map(proizvodjac -> new ProizvodjacDto(proizvodjac.getId(), proizvodjac.getName()))
                .toList();
    }
}
