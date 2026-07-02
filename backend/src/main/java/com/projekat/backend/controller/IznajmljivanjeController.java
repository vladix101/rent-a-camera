package com.projekat.backend.controller;

import com.projekat.backend.dto.EmployeeDashboardStatsDto;
import com.projekat.backend.dto.IznajmljivanjeDto;
import com.projekat.backend.dto.IznajmljivanjeRequestDto;
import com.projekat.backend.security.JwtUtil;
import com.projekat.backend.service.IznajmljivanjeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class IznajmljivanjeController {

    private final IznajmljivanjeService iznajmljivanjeService;
    private final JwtUtil jwtUtil;

    @PostMapping("/iznajmljivanja")
    public ResponseEntity<IznajmljivanjeDto> createIznajmljivanje(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody IznajmljivanjeRequestDto requestDto) {
        Long klijentId = jwtUtil.requireUserId(authHeader, "KLIJENT");
        return new ResponseEntity<>(iznajmljivanjeService.createIznajmljivanje(klijentId, requestDto), HttpStatus.CREATED);
    }

    @GetMapping("/iznajmljivanja/moja")
    public List<IznajmljivanjeDto> getMojaIznajmljivanja(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long klijentId = jwtUtil.requireUserId(authHeader, "KLIJENT");
        return iznajmljivanjeService.getMojaIznajmljivanja(klijentId);
    }

    @GetMapping("/iznajmljivanja/statistika")
    public EmployeeDashboardStatsDto getStatistika(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datumOd,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datumDo) {
        jwtUtil.requireUserId(authHeader, "ZAPOSLENI");
        return iznajmljivanjeService.getStatistika(datumOd, datumDo);
    }
}
