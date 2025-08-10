package com.musinsa.category.domain.repository;

import com.musinsa.category.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    List<Category> findByParentIdAndIsActiveTrueOrderBySortOrderAsc(Long parentId);
    List<Category> findByAncestorIdAndAndIsActiveTrueOrderBySortOrderAsc(Long ancestorId);
    List<Category> findByParentIdIsNullOrderBySortOrderAsc();
    Optional<Category> findBySlug(String slug);
}