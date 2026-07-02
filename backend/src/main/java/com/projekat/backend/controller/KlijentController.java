package com.projekat.backend.controller;

import com.projekat.backend.dto.KlijentDto;
import com.projekat.backend.service.KlijentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
}
