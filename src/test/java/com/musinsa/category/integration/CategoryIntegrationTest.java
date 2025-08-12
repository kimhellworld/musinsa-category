package com.musinsa.category.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musinsa.category.domain.entity.Category;
import com.musinsa.category.domain.repository.CategoryRepository;
import com.musinsa.category.dto.request.CategoryRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CategoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("카테고리 전체 플로우 - 생성, 조회, 수정, 삭제")
    void categoryFullFlow() throws Exception {
        // 1. 최상위 카테고리 생성
        CategoryRequest rootRequest = new CategoryRequest("전자제품", "electronics", null, 1);
        
        String rootResponse = mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rootRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("전자제품"))
                .andExpect(jsonPath("$.slug").value("electronics"))
                .andExpect(jsonPath("$.parentId").isEmpty())
                .andExpect(jsonPath("$.order").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long rootId = objectMapper.readTree(rootResponse).get("id").asLong();

        // 2. 하위 카테고리 생성
        CategoryRequest childRequest = new CategoryRequest("컴퓨터", "computer", rootId, 1);
        
        String childResponse = mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(childRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("컴퓨터"))
                .andExpect(jsonPath("$.slug").value("computer"))
                .andExpect(jsonPath("$.parentId").value(rootId))
                .andExpect(jsonPath("$.order").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long childId = objectMapper.readTree(childResponse).get("id").asLong();

        // 3. 손자 카테고리 생성
        CategoryRequest grandChildRequest = new CategoryRequest("노트북", "laptop", childId, 1);
        
        String grandChildResponse = mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(grandChildRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("노트북"))
                .andExpect(jsonPath("$.slug").value("laptop"))
                .andExpect(jsonPath("$.parentId").value(childId))
                .andExpect(jsonPath("$.order").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long grandChildId = objectMapper.readTree(grandChildResponse).get("id").asLong();

        // 4. 전체 카테고리 조회 - 트리 구조 확인
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(rootId))
                .andExpect(jsonPath("$.data[0].name").value("전자제품"))
                .andExpect(jsonPath("$.data[0].children", hasSize(1)))
                .andExpect(jsonPath("$.data[0].children[0].id").value(childId))
                .andExpect(jsonPath("$.data[0].children[0].name").value("컴퓨터"))
                .andExpect(jsonPath("$.data[0].children[0].children", hasSize(1)))
                .andExpect(jsonPath("$.data[0].children[0].children[0].id").value(grandChildId))
                .andExpect(jsonPath("$.data[0].children[0].children[0].name").value("노트북"));

        // 5. 특정 카테고리와 하위 카테고리 조회
        mockMvc.perform(get("/api/categories/{id}", rootId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(rootId))
                .andExpect(jsonPath("$.name").value("전자제품"))
                .andExpect(jsonPath("$.children", hasSize(1)))
                .andExpect(jsonPath("$.children[0].id").value(childId))
                .andExpect(jsonPath("$.children[0].children", hasSize(1)))
                .andExpect(jsonPath("$.children[0].children[0].id").value(grandChildId));

        // 6. 카테고리 수정
        CategoryRequest updateRequest = new CategoryRequest("데스크탑 컴퓨터", "desktop-computer", rootId, 2);
        
        mockMvc.perform(put("/api/categories/{id}", childId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(childId))
                .andExpect(jsonPath("$.name").value("데스크탑 컴퓨터"))
                .andExpect(jsonPath("$.slug").value("desktop-computer"))
                .andExpect(jsonPath("$.order").value(2));

        // 7. 하위 카테고리가 있는 카테고리 삭제 시도 (실패해야 함)
        mockMvc.perform(delete("/api/categories/{id}", childId))
                .andExpect(status().isBadRequest());

        // 8. 최하위 카테고리부터 삭제
        mockMvc.perform(delete("/api/categories/{id}", grandChildId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        // 9. 중간 카테고리 삭제
        mockMvc.perform(delete("/api/categories/{id}", childId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        // 10. 최상위 카테고리 삭제
        mockMvc.perform(delete("/api/categories/{id}", rootId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        // 11. 모든 카테고리가 삭제되었는지 확인
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    @DisplayName("다중 루트 카테고리와 계층 구조")
    void multipleRootCategoriesWithHierarchy() throws Exception {
        // 첫 번째 루트 카테고리와 하위 구조 생성
        CategoryRequest electronics = new CategoryRequest("전자제품", "electronics", null, 1);
        String electronicsResponse = mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(electronics)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long electronicsId = objectMapper.readTree(electronicsResponse).get("id").asLong();

        CategoryRequest computer = new CategoryRequest("컴퓨터", "computer", electronicsId, 1);
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(computer)))
                .andExpect(status().isCreated());

        // 두 번째 루트 카테고리와 하위 구조 생성
        CategoryRequest clothing = new CategoryRequest("의류", "clothing", null, 2);
        String clothingResponse = mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clothing)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long clothingId = objectMapper.readTree(clothingResponse).get("id").asLong();

        CategoryRequest shirt = new CategoryRequest("셔츠", "shirt", clothingId, 1);
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shirt)))
                .andExpect(status().isCreated());

        // 전체 카테고리 조회 - 두 개의 루트 카테고리 확인
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].name").value("전자제품"))
                .andExpect(jsonPath("$.data[0].children", hasSize(1)))
                .andExpect(jsonPath("$.data[0].children[0].name").value("컴퓨터"))
                .andExpect(jsonPath("$.data[1].name").value("의류"))
                .andExpect(jsonPath("$.data[1].children", hasSize(1)))
                .andExpect(jsonPath("$.data[1].children[0].name").value("셔츠"));
    }

    @Test
    @DisplayName("에러 케이스 - 존재하지 않는 카테고리")
    void errorCases() throws Exception {
        // 존재하지 않는 카테고리 조회
        mockMvc.perform(get("/api/categories/{id}", 999L))
                .andExpect(status().isNotFound());

        // 존재하지 않는 카테고리 수정
        CategoryRequest updateRequest = new CategoryRequest("수정", "update", null, 1);
        mockMvc.perform(put("/api/categories/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());

        // 존재하지 않는 카테고리 삭제
        mockMvc.perform(delete("/api/categories/{id}", 999L))
                .andExpect(status().isNotFound());

        // 존재하지 않는 부모 카테고리로 생성
        CategoryRequest invalidParent = new CategoryRequest("테스트", "test", 999L, 1);
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidParent)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("유효성 검증 - 필수 필드 누락")
    void validationTest() throws Exception {
        // name 누락
        CategoryRequest noName = new CategoryRequest(null, "test", null, 1);
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noName)))
                .andExpect(status().isBadRequest());

        // slug 누락
        CategoryRequest noSlug = new CategoryRequest("테스트", null, null, 1);
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noSlug)))
                .andExpect(status().isBadRequest());
    }
}