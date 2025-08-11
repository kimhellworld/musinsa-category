package com.musinsa.category.controller;

import com.musinsa.category.dto.request.CategoryRequest;
import com.musinsa.category.dto.response.CategoryResponse;
import com.musinsa.category.dto.response.CategoryTreeResponse;
import com.musinsa.category.dto.response.PagingResponse;
import com.musinsa.category.service.CategoryFetchingService;
import com.musinsa.category.service.CategoryUpdatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryUpdatingService categoryUpdatingService;
    private final CategoryFetchingService categoryFetchingService;

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        return new ResponseEntity<>(categoryUpdatingService.createCategory(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryUpdatingService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryUpdatingService.deleteCategory(id));
    }

    @GetMapping
    public ResponseEntity<PagingResponse<CategoryTreeResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryFetchingService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryTreeResponse> getCategoryWithChildren(@PathVariable Long id) {
        return ResponseEntity.ok(categoryFetchingService.getCategoryWithChildren(id));
    }
}
