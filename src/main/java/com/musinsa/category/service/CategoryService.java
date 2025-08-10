package com.musinsa.category.service;

import com.musinsa.category.domain.entity.Category;
import com.musinsa.category.domain.repository.CategoryRepository;
import com.musinsa.category.dto.request.CategoryRequest;
import com.musinsa.category.dto.response.CategoryResponse;
import com.musinsa.category.exception.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 카테고리 추가
     * @param request
     * @return
     */
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Long ancestorId = null;
        // 부모 카테고리 검증
        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.getParentId()));

            ancestorId = parent.getAncestorId() != null ? parent.getAncestorId() : parent.getId();
        }
        
        Category category = Category.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .parentId(request.getParentId())
                .ancestorId(ancestorId)
                .sortOrder(request.getOrder())
                .build();
        Category savedCategory = categoryRepository.save(category);
        return CategoryResponse.from(savedCategory);
    }

    /**
     * 카테고리 수정
     * @param id
     * @param request
     * @return
     */
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);
        
        category.update(request.getName(), request.getSlug(), request.getParentId(), request.getOrder());
        return CategoryResponse.from(category);
    }

    /**
     * 카테고리 삭제
     * @param id
     * @return
     */
    @Transactional
    public boolean deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException();
        }

        categoryRepository.deleteById(id);
        return true;
    }
}