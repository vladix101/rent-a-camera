package com.projekat.backend.controller;

import com.projekat.backend.dto.LoginRequestDto;
import com.projekat.backend.dto.LoginResponseDto;
import com.projekat.backend.dto.ZaposleniDto;
import com.projekat.backend.service.ZaposleniService;
import lombok.RequiredArgsConstructor;
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
public class ZaposleniController {

    private final ZaposleniService zaposleniService;

    @GetMapping("/zaposleni")
    public List<ZaposleniDto> getZaposleni() {
        return zaposleniService.getZaposleni();
    }

    @PostMapping("/zaposleni/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequestDto) {
        return zaposleniService.login(loginRequestDto);
    }
}
