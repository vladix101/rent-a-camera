package com.projekat.backend.controller;

import com.projekat.backend.dto.CategoryDto;
import com.projekat.backend.security.JwtUtil;
import com.projekat.backend.service.CategoryService;
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
public class CategoryController {

    private final CategoryService categoryService;
    private final JwtUtil jwtUtil;

    @GetMapping("/categories")
    public List<CategoryDto> getCategories() {
        return categoryService.getCategories();
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryDto> createCategory(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CategoryDto requestDto) {
        jwtUtil.requireUserId(authHeader, "EMPLOYEE");
        return new ResponseEntity<>(categoryService.createCategory(requestDto), HttpStatus.CREATED);
    }
}
