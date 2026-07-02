package com.projekat.backend.service;

import com.projekat.backend.dto.LoginRequestDto;
import com.projekat.backend.dto.LoginResponseDto;
import com.projekat.backend.dto.ZaposleniDto;
import com.projekat.backend.entity.Zaposleni;
import com.projekat.backend.repository.ZaposleniRepository;
import com.projekat.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ZaposleniService {

    private final ZaposleniRepository zaposleniRepository;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public List<ZaposleniDto> getZaposleni() {
        return zaposleniRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Zaposleni zaposleni = zaposleniRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Pogrešno korisničko ime ili lozinka"));

        if (!zaposleni.getPassword().equals(loginRequestDto.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Pogrešno korisničko ime ili lozinka");
        }

        String token = jwtUtil.generateToken(zaposleni.getId(), zaposleni.getUsername(), zaposleni.getIme(), zaposleni.getPrezime(), "ZAPOSLENI");
        return new LoginResponseDto(zaposleni.getId(), zaposleni.getIme(), zaposleni.getPrezime(), zaposleni.getUsername(), "ZAPOSLENI", token);
    }

    private ZaposleniDto toDto(Zaposleni zaposleni) {
        return new ZaposleniDto(zaposleni.getId(), zaposleni.getIme(), zaposleni.getPrezime(),
                zaposleni.getUsername(), zaposleni.getEmail());
    }
}
