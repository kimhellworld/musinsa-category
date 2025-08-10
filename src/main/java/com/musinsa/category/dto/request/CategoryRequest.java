package com.musinsa.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
    
    @NotBlank(message = "카테고리 이름은 필수입니다")
    private String name;

    @NotBlank(message = "카테고리 slug는 필수입니다")
    private String slug;
    
    private Long parentId;

    // 0 이상만 가능
    private Integer order;
}