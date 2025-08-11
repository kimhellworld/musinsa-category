package com.musinsa.category.controller;

import com.musinsa.category.dto.request.CategoryRequest;
import com.musinsa.category.dto.response.CategoryResponse;
import com.musinsa.category.dto.response.CategoryTreeResponse;
import com.musinsa.category.dto.response.PagingResponse;
import com.musinsa.category.service.CategoryFetchingService;
import com.musinsa.category.service.CategoryUpdatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "카테고리 API", description = "카테고리 생성, 조회, 수정, 삭제 API")
public class CategoryController {

    private final CategoryUpdatingService categoryUpdatingService;
    private final CategoryFetchingService categoryFetchingService;

    @Operation(summary = "카테고리 생성", description = "새로운 카테고리를 생성합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201",
                description = "카테고리 생성 성공",
                content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Parameter(description = "카테고리 생성 정보", required = true)
            @Valid @RequestBody CategoryRequest request) {
        return new ResponseEntity<>(categoryUpdatingService.createCategory(request), HttpStatus.CREATED);
    }

    @Operation(summary = "카테고리 수정", description = "기존 카테고리 정보를 수정합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                description = "카테고리 수정 성공",
                content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @Parameter(description = "수정할 카테고리 ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "카테고리 수정 정보", required = true)
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryUpdatingService.updateCategory(id, request));
    }

    @Operation(summary = "카테고리 삭제", description = "카테고리를 삭제합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                description = "카테고리 삭제 성공",
                content = @Content(schema = @Schema(implementation = Boolean.class))),
        @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteCategory(
            @Parameter(description = "삭제할 카테고리 ID", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(categoryUpdatingService.deleteCategory(id));
    }

    @Operation(summary = "전체 카테고리 목록 조회", description = "계층 구조로 된 전체 카테고리 목록을 조회합니다")
    @ApiResponse(responseCode = "200",
            description = "카테고리 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = PagingResponse.class)))
    @GetMapping
    public ResponseEntity<PagingResponse<CategoryTreeResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryFetchingService.getAllCategories());
    }

    @Operation(summary = "특정 카테고리와 하위 카테고리 조회", description = "특정 카테고리와 그 하위 카테고리 목록을 트리 구조로 조회합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                description = "카테고리 조회 성공",
                content = @Content(schema = @Schema(implementation = CategoryTreeResponse.class))),
        @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryTreeResponse> getCategoryWithChildren(
            @Parameter(description = "조회할 카테고리 ID", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(categoryFetchingService.getCategoryWithChildren(id));
    }
}
