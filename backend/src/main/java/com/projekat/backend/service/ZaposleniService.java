package com.projekat.backend.service;

import com.projekat.backend.dto.ZaposleniDto;
import com.projekat.backend.entity.Zaposleni;
import com.projekat.backend.repository.ZaposleniRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ZaposleniService {

    private final ZaposleniRepository zaposleniRepository;

    @Transactional(readOnly = true)
    public List<ZaposleniDto> getZaposleni() {
        return zaposleniRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    private ZaposleniDto toDto(Zaposleni zaposleni) {
        return new ZaposleniDto(zaposleni.getId(), zaposleni.getIme(), zaposleni.getPrezime(),
                zaposleni.getUsername(), zaposleni.getEmail());
    }
}
