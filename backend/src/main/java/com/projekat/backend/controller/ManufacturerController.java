package com.projekat.backend.controller;

import com.projekat.backend.dto.ManufacturerDto;
import com.projekat.backend.service.ManufacturerService;
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
public class ManufacturerController {

    private final ManufacturerService manufacturerService;

    @GetMapping("/manufacturers")
    public List<ManufacturerDto> getManufacturers() {
        return manufacturerService.getManufacturers();
    }
}
