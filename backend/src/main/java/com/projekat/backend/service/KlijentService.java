package com.projekat.backend.service;

import com.projekat.backend.dto.KlijentDto;
import com.projekat.backend.entity.Klijent;
import com.projekat.backend.repository.KlijentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KlijentService {

    private final KlijentRepository klijentRepository;

    @Transactional(readOnly = true)
    public List<KlijentDto> getKlijenti() {
        return klijentRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    private KlijentDto toDto(Klijent klijent) {
        return new KlijentDto(klijent.getId(), klijent.getIme(), klijent.getPrezime(),
                klijent.getStarost(), klijent.getUsername(), klijent.getEmail());
    }
}
