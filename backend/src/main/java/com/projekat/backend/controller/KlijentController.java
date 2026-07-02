package com.projekat.backend.controller;

import com.projekat.backend.dto.KlijentDto;
import com.projekat.backend.dto.KlijentRegistrationDto;
import com.projekat.backend.dto.KlijentVerificationDto;
import com.projekat.backend.dto.LoginRequestDto;
import com.projekat.backend.dto.LoginResponseDto;
import com.projekat.backend.dto.MessageResponseDto;
import com.projekat.backend.service.KlijentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class KlijentController {

    private final KlijentService klijentService;

    @GetMapping("/klijenti")
    public List<KlijentDto> getKlijenti() {
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
}
