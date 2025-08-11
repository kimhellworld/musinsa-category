package com.musinsa.category.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE categories SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ancestor_id")
    private Long ancestorId;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "slug", nullable = false, length = 150)
    private String slug;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public Category(Long ancestorId, Long parentId, String name, String slug, Integer sortOrder, Boolean isActive) {
        this.ancestorId = ancestorId;
        this.parentId = parentId;
        this.name = name;
        this.slug = slug;
        this.sortOrder = sortOrder == null ? 0 : sortOrder;
        this.isActive = isActive == null ? true : isActive;
    }

    public void update(String name, String slug, Long parentId, Long ancestorId, Integer sortOrder) {
        this.name = name;
        this.slug = slug;
        this.parentId = parentId;
        this.ancestorId = ancestorId;
        this.sortOrder = sortOrder;
    }

    public void inactive(){
        this.isActive = false;
    }

    public boolean hasParent() {
        return this.parentId != null;
    }
}