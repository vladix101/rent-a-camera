package com.projekat.backend.controller;

import com.projekat.backend.dto.KategorijaDto;
import com.projekat.backend.service.KategorijaService;
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
public class KategorijaController {

    private final KategorijaService kategorijaService;

    @GetMapping("/kategorije")
    public List<KategorijaDto> getKategorije() {
        return kategorijaService.getKategorije();
    }
}
