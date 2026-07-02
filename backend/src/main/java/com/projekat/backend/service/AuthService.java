package com.projekat.backend.service;

import com.projekat.backend.dto.LoginRequestDto;
import com.projekat.backend.dto.LoginResponseDto;
import com.projekat.backend.entity.Klijent;
import com.projekat.backend.entity.Zaposleni;
import com.projekat.backend.repository.KlijentRepository;
import com.projekat.backend.repository.ZaposleniRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KlijentRepository klijentRepository;
    private final ZaposleniRepository zaposleniRepository;

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Optional<Klijent> klijent = klijentRepository.findByUsername(loginRequestDto.getUsername());
        if (klijent.isPresent() && klijent.get().getPassword().equals(loginRequestDto.getPassword())) {
            Klijent found = klijent.get();
            return new LoginResponseDto(found.getId(), found.getIme(), found.getPrezime(), found.getUsername(), "KLIJENT");
        }

        Optional<Zaposleni> zaposleni = zaposleniRepository.findByUsername(loginRequestDto.getUsername());
        if (zaposleni.isPresent() && zaposleni.get().getPassword().equals(loginRequestDto.getPassword())) {
            Zaposleni found = zaposleni.get();
            return new LoginResponseDto(found.getId(), found.getIme(), found.getPrezime(), found.getUsername(), "ZAPOSLENI");
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Pogrešno korisničko ime ili lozinka");
    }
}
