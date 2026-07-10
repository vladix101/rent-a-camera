package com.projekat.backend.controller;

import com.projekat.backend.dto.ProizvodjacDto;
import com.projekat.backend.service.ProizvodjacService;
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
public class ProizvodjacController {

    private final ProizvodjacService proizvodjacService;

    @GetMapping("/proizvodjaci")
    public List<ProizvodjacDto> getProizvodjaci() {
        return proizvodjacService.getProizvodjaci();
    }
}
