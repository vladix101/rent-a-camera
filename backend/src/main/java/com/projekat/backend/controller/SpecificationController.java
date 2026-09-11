package com.projekat.backend.controller;

import com.projekat.backend.dto.SpecificationDto;
import com.projekat.backend.security.JwtUtil;
import com.projekat.backend.service.SpecificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SpecificationController {

    private final SpecificationService specificationService;
    private final JwtUtil jwtUtil;

    @GetMapping("/specifications")
    public List<SpecificationDto> getSpecifications() {
        return specificationService.getSpecifications();
    }

    @PostMapping("/specifications")
    public ResponseEntity<SpecificationDto> createSpecification(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody SpecificationDto requestDto) {
        jwtUtil.requireUserId(authHeader, "EMPLOYEE");
        return new ResponseEntity<>(specificationService.createSpecification(requestDto), HttpStatus.CREATED);
    }
}
