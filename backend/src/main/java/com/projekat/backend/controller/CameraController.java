package com.projekat.backend.controller;

import com.projekat.backend.dto.CameraDto;
import com.projekat.backend.dto.CameraRequestDto;
import com.projekat.backend.dto.OccupancyDto;
import com.projekat.backend.security.JwtUtil;
import com.projekat.backend.service.CameraService;
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
public class CameraController {

    private final CameraService cameraService;
    private final JwtUtil jwtUtil;

    @GetMapping("/cameras")
    public List<CameraDto> getCameras(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        return cameraService.getCameras(dateFrom, dateTo);
    }

    @GetMapping("/cameras/{id}/occupancy")
    public List<OccupancyDto> getOccupancy(@PathVariable Long id) {
        return cameraService.getOccupiedPeriods(id);
    }

    @PostMapping("/cameras")
    public ResponseEntity<CameraDto> createCamera(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CameraRequestDto requestDto) {
        jwtUtil.requireUserId(authHeader, "EMPLOYEE");
        return new ResponseEntity<>(cameraService.createCamera(requestDto), HttpStatus.CREATED);
    }

    @PutMapping("/cameras/{id}")
    public CameraDto updateCamera(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @RequestBody CameraRequestDto requestDto) {
        jwtUtil.requireUserId(authHeader, "EMPLOYEE");
        return cameraService.updateCamera(id, requestDto);
    }

    @DeleteMapping("/cameras/{id}")
    public ResponseEntity<Void> deleteCamera(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        jwtUtil.requireUserId(authHeader, "EMPLOYEE");
        cameraService.deleteCamera(id);
        return ResponseEntity.noContent().build();
    }
}
