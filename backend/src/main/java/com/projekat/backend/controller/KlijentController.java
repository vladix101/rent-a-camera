package com.projekat.backend.controller;

import com.projekat.backend.dto.KlijentDto;
import com.projekat.backend.dto.KlijentRegistrationDto;
import com.projekat.backend.dto.KlijentUpdateDto;
import com.projekat.backend.dto.KlijentVerificationDto;
import com.projekat.backend.dto.LoginRequestDto;
import com.projekat.backend.dto.LoginResponseDto;
import com.projekat.backend.dto.MessageResponseDto;
import com.projekat.backend.security.JwtUtil;
import com.projekat.backend.service.KlijentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class KlijentController {

    private final KlijentService klijentService;
    private final JwtUtil jwtUtil;

    @GetMapping("/klijenti")
    public List<KlijentDto> getKlijenti(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        jwtUtil.requireUserId(authHeader, "ZAPOSLENI");
        return klijentService.getKlijenti();
    }

    @PostMapping("/klijenti/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequestDto) {
        return klijentService.login(loginRequestDto);
    }

    @PostMapping("/klijenti/register")
    public ResponseEntity<MessageResponseDto> startRegistration(@RequestBody KlijentRegistrationDto registrationDto) {
        return ResponseEntity.ok(klijentService.startRegistration(registrationDto));
    }

    @PostMapping("/klijenti/register/verify")
    public ResponseEntity<KlijentDto> verifyRegistration(@RequestBody KlijentVerificationDto verificationDto) {
        return new ResponseEntity<>(klijentService.verifyRegistration(verificationDto), HttpStatus.CREATED);
    }

    @PutMapping("/klijenti/{id}")
    public KlijentDto updateKlijent(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @RequestBody KlijentUpdateDto updateDto) {
        jwtUtil.requireUserId(authHeader, "ZAPOSLENI");
        return klijentService.updateKlijent(id, updateDto);
    }

    @DeleteMapping("/klijenti/{id}")
    public ResponseEntity<Void> deleteKlijent(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        jwtUtil.requireUserId(authHeader, "ZAPOSLENI");
        klijentService.deleteKlijent(id);
        return ResponseEntity.noContent().build();
    }
}
