package com.projekat.backend.service;

import com.projekat.backend.dto.KategorijaDto;
import com.projekat.backend.entity.Kategorija;
import com.projekat.backend.repository.KategorijaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KategorijaService {

    private final KategorijaRepository kategorijaRepository;

    @Transactional(readOnly = true)
    public List<KategorijaDto> getKategorije() {
        return kategorijaRepository.findAll()
                .stream()
                .map(kategorija -> new KategorijaDto(kategorija.getId(), kategorija.getNaziv()))
                .toList();
    }
}
