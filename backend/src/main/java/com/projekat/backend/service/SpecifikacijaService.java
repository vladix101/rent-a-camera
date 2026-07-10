package com.projekat.backend.service;

import com.projekat.backend.dto.SpecifikacijaDto;
import com.projekat.backend.entity.Specifikacija;
import com.projekat.backend.exception.ValidationException;
import com.projekat.backend.repository.SpecifikacijaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SpecifikacijaService {

    private final SpecifikacijaRepository specifikacijaRepository;

    @Transactional(readOnly = true)
    public List<SpecifikacijaDto> getSpecifikacije() {
        return specifikacijaRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public SpecifikacijaDto createSpecifikacija(SpecifikacijaDto requestDto) {
        validateSpecifikacija(requestDto);

        Specifikacija specifikacija = new Specifikacija();
        specifikacija.setRezolucija(requestDto.getRezolucija());
        specifikacija.setSenzorSlike(requestDto.getSenzorSlike());
        specifikacija.setWifi(requestDto.getWifi() != null && requestDto.getWifi());
        specifikacija.setEkran(requestDto.getEkran());
        specifikacija.setNapajanje(requestDto.getNapajanje());
        specifikacija.setVelicinaSlike(requestDto.getVelicinaSlike());
        specifikacija.setOpis(requestDto.getOpis());

        specifikacija = specifikacijaRepository.save(specifikacija);
        return toDto(specifikacija);
    }

    private void validateSpecifikacija(SpecifikacijaDto requestDto) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        if (requestDto.getRezolucija() == null || requestDto.getRezolucija().isBlank()) {
            fieldErrors.put("rezolucija", "Rezolucija je obavezna");
        }
        if (requestDto.getSenzorSlike() == null || requestDto.getSenzorSlike().isBlank()) {
            fieldErrors.put("senzorSlike", "Senzor slike je obavezan");
        }

        if (!fieldErrors.isEmpty()) {
            throw new ValidationException(fieldErrors);
        }
    }

    private SpecifikacijaDto toDto(Specifikacija specifikacija) {
        return new SpecifikacijaDto(
                specifikacija.getId(),
                specifikacija.getRezolucija(),
                specifikacija.getSenzorSlike(),
                specifikacija.getWifi(),
                specifikacija.getEkran(),
                specifikacija.getNapajanje(),
                specifikacija.getVelicinaSlike(),
                specifikacija.getOpis()
        );
    }
}
