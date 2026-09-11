package com.projekat.backend.controller;

import com.projekat.backend.dto.ClientDto;
import com.projekat.backend.dto.ClientRegistrationDto;
import com.projekat.backend.dto.ClientUpdateDto;
import com.projekat.backend.dto.ClientVerificationDto;
import com.projekat.backend.dto.LoginRequestDto;
import com.projekat.backend.dto.LoginResponseDto;
import com.projekat.backend.dto.MessageResponseDto;
import com.projekat.backend.security.JwtUtil;
import com.projekat.backend.service.ClientService;
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
public class ClientController {

    private final ClientService clientService;
    private final JwtUtil jwtUtil;

    @GetMapping("/clients")
    public List<ClientDto> getClients(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        jwtUtil.requireUserId(authHeader, "EMPLOYEE");
        return clientService.getClients();
    }

    @PostMapping("/clients/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequestDto) {
        return clientService.login(loginRequestDto);
    }

    @PostMapping("/clients/register")
    public ResponseEntity<MessageResponseDto> startRegistration(@RequestBody ClientRegistrationDto registrationDto) {
        return ResponseEntity.ok(clientService.startRegistration(registrationDto));
    }

    @PostMapping("/clients/register/verify")
    public ResponseEntity<ClientDto> verifyRegistration(@RequestBody ClientVerificationDto verificationDto) {
        return new ResponseEntity<>(clientService.verifyRegistration(verificationDto), HttpStatus.CREATED);
    }

    @PutMapping("/clients/{id}")
    public ClientDto updateClient(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @RequestBody ClientUpdateDto updateDto) {
        jwtUtil.requireUserId(authHeader, "EMPLOYEE");
        return clientService.updateClient(id, updateDto);
    }

    @DeleteMapping("/clients/{id}")
    public ResponseEntity<Void> deleteClient(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        jwtUtil.requireUserId(authHeader, "EMPLOYEE");
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
