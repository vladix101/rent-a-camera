package com.projekat.backend.service;

import com.projekat.backend.dto.KategorijaDto;
import com.projekat.backend.entity.Kategorija;
import com.projekat.backend.exception.ValidationException;
import com.projekat.backend.repository.KategorijaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @Transactional
    public KategorijaDto createKategorija(KategorijaDto requestDto) {
        if (requestDto == null || requestDto.getNaziv() == null || requestDto.getNaziv().isBlank()) {
            Map<String, String> fieldErrors = new LinkedHashMap<>();
            fieldErrors.put("naziv", "Naziv kategorije je obavezan");
            throw new ValidationException(fieldErrors);
        }

        Kategorija kategorija = new Kategorija();
        kategorija.setNaziv(requestDto.getNaziv().trim());
        kategorija = kategorijaRepository.save(kategorija);

        return new KategorijaDto(kategorija.getId(), kategorija.getNaziv());
    }
}
