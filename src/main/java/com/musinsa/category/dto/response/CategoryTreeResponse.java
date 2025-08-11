package com.musinsa.category.dto.response;

import com.musinsa.category.domain.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTreeResponse {
    
    private Long id;
    private String name;
    private String slug;
    private Integer order;
    @Builder.Default
    private List<CategoryTreeResponse> children = new ArrayList<>();
    
    public static CategoryTreeResponse from(Category category) {
        return CategoryTreeResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .order(category.getSortOrder())
                .build();
    }
    
    public void addChild(CategoryTreeResponse child) {
        this.children.add(child);
    }
}