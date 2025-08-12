package com.musinsa.category.service;

import com.musinsa.category.domain.entity.Category;
import com.musinsa.category.domain.repository.CategoryRepository;
import com.musinsa.category.dto.request.CategoryRequest;
import com.musinsa.category.dto.response.CategoryResponse;
import com.musinsa.category.exception.CategoryCannotDeleteException;
import com.musinsa.category.exception.CategoryNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryUpdatingServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryUpdatingService categoryUpdatingService;

    private Category parentCategory;
    private Category childCategory;

    @BeforeEach
    void setUp() throws Exception {
        parentCategory = Category.builder()
                .name("전자제품")
                .slug("electronics")
                .sortOrder(1)
                .build();
        setId(parentCategory, 1L);

        childCategory = Category.builder()
                .parentId(1L)
                .name("컴퓨터")
                .slug("computer")
                .sortOrder(1)
                .build();
        setId(childCategory, 2L);
    }
    
    private void setId(Category category, Long id) throws Exception {
        var field = Category.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(category, id);
    }

    @Test
    @DisplayName("카테고리 생성 - 성공")
    void createCategory_Success() throws Exception {
        // given
        CategoryRequest request = new CategoryRequest("스마트폰", "smartphone", 1L, 2);
        Category savedCategory = Category.builder()
                .name("스마트폰")
                .slug("smartphone")
                .parentId(1L)
                .sortOrder(2)
                .build();
        setId(savedCategory, 3L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        // when
        CategoryResponse response = categoryUpdatingService.createCategory(request);

        // then
        assertThat(response.getId()).isEqualTo(3L);
        assertThat(response.getName()).isEqualTo("스마트폰");
        assertThat(response.getSlug()).isEqualTo("smartphone");
        assertThat(response.getParentId()).isEqualTo(1L);
        assertThat(response.getOrder()).isEqualTo(2);
        
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("카테고리 생성 - 최상위 카테고리")
    void createCategory_RootCategory() throws Exception {
        // given
        CategoryRequest request = new CategoryRequest("의류", "clothing", null, 1);
        Category savedCategory = Category.builder()
                .name("의류")
                .slug("clothing")
                .sortOrder(1)
                .build();
        setId(savedCategory, 4L);

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        // when
        CategoryResponse response = categoryUpdatingService.createCategory(request);

        // then
        assertThat(response.getId()).isEqualTo(4L);
        assertThat(response.getName()).isEqualTo("의류");
        assertThat(response.getSlug()).isEqualTo("clothing");
        assertThat(response.getParentId()).isNull();
        assertThat(response.getOrder()).isEqualTo(1);
        
        verify(categoryRepository).save(any(Category.class));
        verify(categoryRepository, never()).findById(any());
    }

    @Test
    @DisplayName("카테고리 생성 - 부모 카테고리 없음")
    void createCategory_ParentNotFound() {
        // given
        CategoryRequest request = new CategoryRequest("스마트폰", "smartphone", 999L, 2);
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> categoryUpdatingService.createCategory(request))
                .isInstanceOf(CategoryNotFoundException.class);
        
        verify(categoryRepository).findById(999L);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("카테고리 수정 - 성공")
    void updateCategory_Success() {
        // given
        Long categoryId = 2L;
        CategoryRequest request = new CategoryRequest("데스크탑 컴퓨터", "desktop-computer", 1L, 3);
        
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(childCategory));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));

        // when
        CategoryResponse response = categoryUpdatingService.updateCategory(categoryId, request);

        // then
        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getName()).isEqualTo("데스크탑 컴퓨터");
        assertThat(response.getSlug()).isEqualTo("desktop-computer");
        assertThat(response.getParentId()).isEqualTo(1L);
        assertThat(response.getOrder()).isEqualTo(3);
        
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).findById(1L);
    }

    @Test
    @DisplayName("카테고리 수정 - 카테고리 없음")
    void updateCategory_CategoryNotFound() {
        // given
        Long categoryId = 999L;
        CategoryRequest request = new CategoryRequest("데스크탑 컴퓨터", "desktop-computer", 1L, 3);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> categoryUpdatingService.updateCategory(categoryId, request))
                .isInstanceOf(CategoryNotFoundException.class);
        
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("카테고리 삭제 - 성공")
    void deleteCategory_Success() {
        // given
        Long categoryId = 2L;
        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(categoryRepository.existsByParentId(categoryId)).thenReturn(false);

        // when
        Boolean result = categoryUpdatingService.deleteCategory(categoryId);

        // then
        assertThat(result).isTrue();
        verify(categoryRepository).existsById(categoryId);
        verify(categoryRepository).existsByParentId(categoryId);
        verify(categoryRepository).deleteById(categoryId);
    }

    @Test
    @DisplayName("카테고리 삭제 - 하위 카테고리 존재")
    void deleteCategory_HasChildren() {
        // given
        Long categoryId = 1L;
        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(categoryRepository.existsByParentId(categoryId)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> categoryUpdatingService.deleteCategory(categoryId))
                .isInstanceOf(CategoryCannotDeleteException.class);
        
        verify(categoryRepository).existsById(categoryId);
        verify(categoryRepository).existsByParentId(categoryId);
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("카테고리 삭제 - 카테고리 없음")
    void deleteCategory_CategoryNotFound() {
        // given
        Long categoryId = 999L;
        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> categoryUpdatingService.deleteCategory(categoryId))
                .isInstanceOf(CategoryNotFoundException.class);
        
        verify(categoryRepository).existsById(categoryId);
        verify(categoryRepository, never()).existsByParentId(any());
        verify(categoryRepository, never()).deleteById(any());
    }
}