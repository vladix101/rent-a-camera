package com.projekat.backend.service;

import com.projekat.backend.dto.CategoryDto;
import com.projekat.backend.entity.Category;
import com.projekat.backend.exception.ValidationException;
import com.projekat.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> new CategoryDto(category.getId(), category.getName()))
                .toList();
    }

    @Transactional
    public CategoryDto createCategory(CategoryDto requestDto) {
        if (requestDto == null || requestDto.getName() == null || requestDto.getName().isBlank()) {
            Map<String, String> fieldErrors = new LinkedHashMap<>();
            fieldErrors.put("name", "Category name is required");
            throw new ValidationException(fieldErrors);
        }

        Category category = new Category();
        category.setName(requestDto.getName().trim());
        category = categoryRepository.save(category);

        return new CategoryDto(category.getId(), category.getName());
    }
}
