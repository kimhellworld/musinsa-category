package com.musinsa.category.service;

import com.musinsa.category.domain.entity.Category;
import com.musinsa.category.domain.repository.CategoryRepository;
import com.musinsa.category.dto.request.CategoryRequest;
import com.musinsa.category.dto.response.CategoryResponse;
import com.musinsa.category.dto.response.CategoryTreeResponse;
import com.musinsa.category.dto.response.PagingResponse;
import com.musinsa.category.exception.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryFetchingService {

    private final CategoryRepository categoryRepository;

    /**
     * 전체 카테고리 목록을 트리 형태로 반환
     * @return
     */
    public PagingResponse<CategoryTreeResponse> getAllCategories() {
        List<Category> allCategories = categoryRepository.findAllByIsActiveTrueOrderBySortOrderAsc();
        return PagingResponse.of(buildResponseAsTree(allCategories));
    }

    /**
     * 특정 카테고리와 그 하위 카테고리 목록을 트리구조로 반환
     * @param id
     * @return
     */
    public CategoryTreeResponse getCategoryWithChildren(Long id) {
        List<Category> categoryWithDescendants = getCategoryWithDescendants(id);
        List<CategoryTreeResponse> treeResponses = buildResponseAsTree(categoryWithDescendants);

        if (treeResponses.isEmpty()) throw new CategoryNotFoundException(id);

        // 요청한 ID와 동일한 카테고리 찾기
        return treeResponses.stream()
                .filter(response -> response.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    /**
     * 재귀 CTE 사용하지 않고 특정 카테고리 Id 기준으로 자기 자신과 모든 하위 카테고리를 평면 리스트로 수집
     * @param id
     * @return
     */
    private List<Category> getCategoryWithDescendants(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);

        // 1. 결과로 반환할 전체 카테고리 리스트를 생성
        List<Category> result = new ArrayList<>();
        result.add(category);

        // 2. 하위 카테고리를 탐색하기 위한 큐(Queue)를 생성하고 시작 카테고리의 ID를 추가
        List<Long> categoriesToSearch = new LinkedList<>();
        categoriesToSearch.add(category.getId());

        // 3. 큐가 빌 때까지 반복하여 모든 하위 카테고리를 탐색
        while (!categoriesToSearch.isEmpty()) {
            // 큐에서 현재 탐색할 부모 카테고리의 ID를 추출.
            List<Long> parentIds = new ArrayList<>(categoriesToSearch);

            // 현재 부모 ID에 해당하는 직계 자식 카테고리들을 DB에서 조회
            List<Category> children = categoryRepository.findAllByParentIdInAndIsActiveTrueOrderBySortOrderAsc(parentIds);
            if(children.isEmpty()) break;

            // 조회된 자식 카테고리들을 전체 결과 리스트에 추가합니다.
            result.addAll(children);

            // 각 자식 카테고리의 ID를 큐에 추가하여 다음 탐색 대상으로 삼습니다.
            categoriesToSearch = children.stream()
                    .map(Category::getId)
                    .collect(Collectors.toList());
        }

        return result;
    }

    private List<CategoryTreeResponse> buildResponseAsTree(List<Category> categories) {
        if (categories.isEmpty()) return Collections.emptyList();
        Map<Long, CategoryTreeResponse> nodeMap = createNodeMap(categories);
        Set<Long> hasParentInList = buildRelationshipsAndGetCategoryIdsWithoutParent(categories, nodeMap);
        return findRootNodes(categories, nodeMap, hasParentInList);
    }

    /**
     * category 목록을 response로 변환
     * search 복잡도 O(1)을 만들기 위해 Map(id : response) 형태로 변환하여 응답
     * @param categories
     * @return
     */
    private Map<Long, CategoryTreeResponse> createNodeMap(List<Category> categories) {
        return categories.stream()
                .collect(Collectors.toMap(
                        Category::getId,
                        CategoryTreeResponse::from
                ));
    }

    /**
     * 1. nodeMap의 요소를 parent와 child 트리구조로 연관관계 매핑
     * 2. 넘겨준 category 목록에서 상위 부모가 list에 있는 카테고리 id만 추출
     * @param categories
     * @param nodeMap
     * @return
     */
    private Set<Long> buildRelationshipsAndGetCategoryIdsWithoutParent(
            List<Category> categories, Map<Long, CategoryTreeResponse> nodeMap) {
        Set<Long> childIds = new HashSet<>();
        for (Category category : categories) {
            Long parentId = category.getParentId();
            if (category.hasParent() && nodeMap.containsKey(parentId)) {
                CategoryTreeResponse parent = nodeMap.get(parentId);
                CategoryTreeResponse child = nodeMap.get(category.getId());
                parent.addChild(child);
                childIds.add(category.getId());
            }
        }
        return childIds;
    }

    /**
     * 루트 노드를 찾아서 반환
     * @param categories
     * @param nodeMap
     * @param childIds
     * @return
     */
    private List<CategoryTreeResponse> findRootNodes(List<Category> categories,
                                                     Map<Long, CategoryTreeResponse> nodeMap,
                                                     Set<Long> childIds) {
        return categories.stream()
                .filter(category -> !childIds.contains(category.getId()))
                .map(category -> nodeMap.get(category.getId()))
                .sorted(Comparator.comparing(CategoryTreeResponse::getOrder))
                .collect(Collectors.toList());
    }
}