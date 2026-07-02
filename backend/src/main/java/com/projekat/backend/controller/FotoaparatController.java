package com.projekat.backend.controller;

import com.projekat.backend.dto.FotoaparatDto;
import com.projekat.backend.dto.FotoaparatRequestDto;
import com.projekat.backend.dto.ZauzetostDto;
import com.projekat.backend.security.JwtUtil;
import com.projekat.backend.service.FotoaparatService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
    private final JwtUtil jwtUtil;

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

    @PostMapping("/fotoaparati")
    public ResponseEntity<FotoaparatDto> createFotoaparat(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody FotoaparatRequestDto requestDto) {
        jwtUtil.requireUserId(authHeader, "ZAPOSLENI");
        return new ResponseEntity<>(fotoaparatService.createFotoaparat(requestDto), HttpStatus.CREATED);
    }

    @PutMapping("/fotoaparati/{id}")
    public FotoaparatDto updateFotoaparat(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @RequestBody FotoaparatRequestDto requestDto) {
        jwtUtil.requireUserId(authHeader, "ZAPOSLENI");
        return fotoaparatService.updateFotoaparat(id, requestDto);
    }

    @DeleteMapping("/fotoaparati/{id}")
    public ResponseEntity<Void> deleteFotoaparat(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        jwtUtil.requireUserId(authHeader, "ZAPOSLENI");
        fotoaparatService.deleteFotoaparat(id);
        return ResponseEntity.noContent().build();
    }
}
