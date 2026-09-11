package com.projekat.backend.service;

import com.projekat.backend.dto.LoginRequestDto;
import com.projekat.backend.dto.LoginResponseDto;
import com.projekat.backend.dto.EmployeeDto;
import com.projekat.backend.entity.Employee;
import com.projekat.backend.repository.EmployeeRepository;
import com.projekat.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployee() {
        return employeeRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Employee employee = employeeRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect username or password"));

        if (!employee.getPassword().equals(loginRequestDto.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect username or password");
        }

        String token = jwtUtil.generateToken(employee.getId(), employee.getUsername(), employee.getFirstName(), employee.getLastName(), "EMPLOYEE");
        return new LoginResponseDto(employee.getId(), employee.getFirstName(), employee.getLastName(), employee.getUsername(), "EMPLOYEE", token);
    }

    private EmployeeDto toDto(Employee employee) {
        return new EmployeeDto(employee.getId(), employee.getFirstName(), employee.getLastName(),
                employee.getUsername(), employee.getEmail());
    }
}
