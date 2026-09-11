package com.projekat.backend.controller;

import com.projekat.backend.dto.EmployeeDashboardStatsDto;
import com.projekat.backend.dto.RentalDto;
import com.projekat.backend.dto.RentalRequestDto;
import com.projekat.backend.security.JwtUtil;
import com.projekat.backend.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
public class RentalController {

    private final RentalService rentalService;
    private final JwtUtil jwtUtil;

    @PostMapping("/rentals")
    public ResponseEntity<RentalDto> createRental(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody RentalRequestDto requestDto) {
        Long clientId = jwtUtil.requireUserId(authHeader, "CLIENT");
        return new ResponseEntity<>(rentalService.createRental(clientId, requestDto), HttpStatus.CREATED);
    }

    @GetMapping("/rentals/my")
    public List<RentalDto> getMyRentals(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long clientId = jwtUtil.requireUserId(authHeader, "CLIENT");
        return rentalService.getMyRentals(clientId);
    }

    @GetMapping("/rentals/stats")
    public EmployeeDashboardStatsDto getStats(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        jwtUtil.requireUserId(authHeader, "EMPLOYEE");
        return rentalService.getStats(dateFrom, dateTo);
    }
}
