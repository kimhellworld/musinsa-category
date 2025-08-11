package com.musinsa.category.domain.repository;

import com.musinsa.category.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    List<Category> findAllByParentIdInAndIsActiveTrueOrderBySortOrderAsc(List<Long> parentIds);
    List<Category> findAllByAncestorIdAndAndIsActiveTrueOrderBySortOrderAsc(Long ancestorId);
    List<Category> findAllByParentIdIsNullAndIsActiveTrueOrderBySortOrderAsc();
    Optional<Category> findBySlug(String slug);

    List<Category> findAllByIsActiveTrueOrderBySortOrderAsc();

    boolean existsByParentId(Long parentId);
}