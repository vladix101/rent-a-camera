package com.projekat.backend.controller;

import com.projekat.backend.dto.FotoaparatDto;
import com.projekat.backend.dto.ZauzetostDto;
import com.projekat.backend.service.FotoaparatService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FotoaparatController {

    private final FotoaparatService fotoaparatService;

    @GetMapping("/fotoaparati")
    public List<FotoaparatDto> getFotoaparati(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datumOd,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datumDo) {
        return fotoaparatService.getFotoaparati(datumOd, datumDo);
    }

    @GetMapping("/fotoaparati/{id}/zauzetost")
    public List<ZauzetostDto> getZauzetost(@PathVariable Long id) {
        return fotoaparatService.getZauzetPeriodi(id);
    }
}
