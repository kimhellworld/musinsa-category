package com.musinsa.category.service;

import com.musinsa.category.domain.entity.Category;
import com.musinsa.category.domain.repository.CategoryRepository;
import com.musinsa.category.dto.response.CategoryTreeResponse;
import com.musinsa.category.dto.response.PagingResponse;
import com.musinsa.category.exception.CategoryNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryFetchingServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryFetchingService categoryFetchingService;

    private Category rootCategory1;
    private Category rootCategory2;
    private Category childCategory1;
    private Category childCategory2;
    private Category grandChildCategory;

    @BeforeEach
    void setUp() throws Exception {
        rootCategory1 = Category.builder()
                .name("전자제품")
                .slug("electronics")
                .sortOrder(1)
                .build();
        setId(rootCategory1, 1L);

        rootCategory2 = Category.builder()
                .name("의류")
                .slug("clothing")
                .sortOrder(2)
                .build();
        setId(rootCategory2, 2L);

        childCategory1 = Category.builder()
                .parentId(1L)
                .name("컴퓨터")
                .slug("computer")
                .sortOrder(1)
                .build();
        setId(childCategory1, 3L);

        childCategory2 = Category.builder()
                .parentId(1L)
                .name("스마트폰")
                .slug("smartphone")
                .sortOrder(2)
                .build();
        setId(childCategory2, 4L);

        grandChildCategory = Category.builder()
                .parentId(3L)
                .name("노트북")
                .slug("laptop")
                .sortOrder(1)
                .build();
        setId(grandChildCategory, 5L);
    }
    
    private void setId(Category category, Long id) throws Exception {
        var field = Category.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(category, id);
    }

    @Test
    @DisplayName("전체 카테고리 조회 - 트리 구조")
    void getAllCategories_TreeStructure() {
        // given
        List<Category> allCategories =
                List.of(rootCategory1, rootCategory2, childCategory1, childCategory2, grandChildCategory);
        when(categoryRepository.findAllByIsActiveTrueOrderBySortOrderAsc()).thenReturn(allCategories);

        // when
        PagingResponse<CategoryTreeResponse> response = categoryFetchingService.getAllCategories();

        // then
        assertThat(response.getData()).hasSize(2); // 루트 카테고리 2개
        
        // 첫 번째 루트 카테고리 검증
        CategoryTreeResponse firstRoot = response.getData().get(0);
        assertThat(firstRoot.getId()).isEqualTo(1L);
        assertThat(firstRoot.getName()).isEqualTo("전자제품");
        assertThat(firstRoot.getChildren()).hasSize(2); // 자식 카테고리 2개
        
        // 첫 번째 자식 카테고리 검증
        CategoryTreeResponse firstChild = firstRoot.getChildren().get(0);
        assertThat(firstChild.getId()).isEqualTo(3L);
        assertThat(firstChild.getName()).isEqualTo("컴퓨터");
        assertThat(firstChild.getChildren()).hasSize(1); // 손자 카테고리 1개
        
        // 손자 카테고리 검증
        CategoryTreeResponse grandChild = firstChild.getChildren().get(0);
        assertThat(grandChild.getId()).isEqualTo(5L);
        assertThat(grandChild.getName()).isEqualTo("노트북");
        assertThat(grandChild.getChildren()).isEmpty();
        
        // 두 번째 루트 카테고리 검증
        CategoryTreeResponse secondRoot = response.getData().get(1);
        assertThat(secondRoot.getId()).isEqualTo(2L);
        assertThat(secondRoot.getName()).isEqualTo("의류");
        assertThat(secondRoot.getChildren()).isEmpty();
    }

    @Test
    @DisplayName("전체 카테고리 조회 - 빈 결과")
    void getAllCategories_EmptyResult() {
        // given
        when(categoryRepository.findAllByIsActiveTrueOrderBySortOrderAsc()).thenReturn(List.of());

        // when
        PagingResponse<CategoryTreeResponse> response = categoryFetchingService.getAllCategories();

        // then
        assertThat(response.getData()).isEmpty();
    }

    @Test
    @DisplayName("특정 카테고리와 하위 카테고리 조회 - 성공")
    void getCategoryWithChildren_Success() {
        // given
        Long categoryId = 1L;
        /**
         * rootCategory1
         *   childCategory1
         *      grandChildCategory
         *   childCategory2
         */
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(rootCategory1));
        when(categoryRepository.findAllByParentIdInAndIsActiveTrueOrderBySortOrderAsc(List.of(categoryId)))
                .thenReturn(List.of(childCategory1, childCategory2));
        when(categoryRepository.findAllByParentIdInAndIsActiveTrueOrderBySortOrderAsc(List.of(childCategory1.getId(), childCategory2.getId())))
                .thenReturn(List.of(grandChildCategory));

        // when
        CategoryTreeResponse response = categoryFetchingService.getCategoryWithChildren(categoryId);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("전자제품");
        assertThat(response.getChildren()).hasSize(2);
        
        // 첫 번째 자식 카테고리 검증
        CategoryTreeResponse firstChild = response.getChildren().get(0);
        assertThat(firstChild.getId()).isEqualTo(3L);
        assertThat(firstChild.getName()).isEqualTo("컴퓨터");
        assertThat(firstChild.getChildren()).hasSize(1);
        
        // 손자 카테고리 검증
        CategoryTreeResponse grandChild = firstChild.getChildren().get(0);
        assertThat(grandChild.getId()).isEqualTo(5L);
        assertThat(grandChild.getName()).isEqualTo("노트북");
    }

    @Test
    @DisplayName("특정 카테고리와 하위 카테고리 조회 - 카테고리 없음")
    void getCategoryWithChildren_CategoryNotFound() {
        // given
        Long categoryId = 999L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> categoryFetchingService.getCategoryWithChildren(categoryId))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    @DisplayName("특정 카테고리와 하위 카테고리 조회 - 자식 없음")
    void getCategoryWithChildren_NoChildren() {
        // given
        Long categoryId = 5L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(grandChildCategory));
        when(categoryRepository.findAllByParentIdInAndIsActiveTrueOrderBySortOrderAsc(List.of(categoryId)))
                .thenReturn(List.of());

        // when
        CategoryTreeResponse response = categoryFetchingService.getCategoryWithChildren(categoryId);

        // then
        assertThat(response.getId()).isEqualTo(5L);
        assertThat(response.getName()).isEqualTo("노트북");
        assertThat(response.getChildren()).isEmpty();
    }
}