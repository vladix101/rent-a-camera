package com.projekat.backend.controller;

import com.projekat.backend.dto.LoginRequestDto;
import com.projekat.backend.dto.LoginResponseDto;
import com.projekat.backend.dto.EmployeeDto;
import com.projekat.backend.service.EmployeeService;
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
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/employee")
    public List<EmployeeDto> getEmployee() {
        return employeeService.getEmployee();
    }

    @PostMapping("/employee/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequestDto) {
        return employeeService.login(loginRequestDto);
    }
}
