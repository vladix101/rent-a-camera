package com.projekat.backend.controller;

import com.projekat.backend.dto.SpecifikacijaDto;
import com.projekat.backend.security.JwtUtil;
import com.projekat.backend.service.SpecifikacijaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SpecifikacijaController {

    private final SpecifikacijaService specifikacijaService;
    private final JwtUtil jwtUtil;

    @GetMapping("/specifikacije")
    public List<SpecifikacijaDto> getSpecifikacije() {
        return specifikacijaService.getSpecifikacije();
    }

    @PostMapping("/specifikacije")
    public ResponseEntity<SpecifikacijaDto> createSpecifikacija(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody SpecifikacijaDto requestDto) {
        jwtUtil.requireUserId(authHeader, "ZAPOSLENI");
        return new ResponseEntity<>(specifikacijaService.createSpecifikacija(requestDto), HttpStatus.CREATED);
    }
}
