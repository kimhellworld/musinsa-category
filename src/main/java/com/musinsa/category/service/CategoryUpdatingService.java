package com.musinsa.category.service;

import com.musinsa.category.domain.entity.Category;
import com.musinsa.category.domain.repository.CategoryRepository;
import com.musinsa.category.dto.request.CategoryRequest;
import com.musinsa.category.dto.response.CategoryResponse;
import com.musinsa.category.exception.CategoryCannotDeleteException;
import com.musinsa.category.exception.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryUpdatingService {

    private final CategoryRepository categoryRepository;

    /**
     * 카테고리 추가
     * @param request
     * @return
     */
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Long ancestorId = getAncestorId(request.getParentId());
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
        Long ancestorId = getAncestorId(request.getParentId());
        category.update(request.getName(), request.getSlug(), request.getParentId(), ancestorId, request.getOrder());
        return CategoryResponse.from(category);
    }

    private Long getAncestorId(Long parentId) {
        if (parentId == null) {
            return null;
        }

        Category parent = categoryRepository.findById(parentId)
                .orElseThrow(() -> new CategoryNotFoundException(parentId));

        return parent.getAncestorId() != null ? parent.getAncestorId() : parent.getId();
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
        if (categoryRepository.existsByParentId(id)){
            throw new CategoryCannotDeleteException("하위 카테고리가 존재하여 삭제가 불가능합니다.");
        }

        categoryRepository.deleteById(id);
        return true;
    }

}