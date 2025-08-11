package com.musinsa.category.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagingResponse<T> {

    private List<T> data;
    private Paging paging;

    public static <T> PagingResponse<T> empty(){
        return new PagingResponse<>(Collections.emptyList(), null);
    }

    public static <T> PagingResponse<T> of(List<T> data, Paging paging) {
        return new PagingResponse<>(data, paging);
    }

    public static <T> PagingResponse<T> of(List<T> data) {
        return  new PagingResponse<>(data, null);
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Paging {
        // null이면 전체
        private Integer pageSize;
        private Cursors cursors;
        private Links links;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Cursors {
        // 숫자 커서 또는 인코딩된 토큰 모두 수용
        private String before;
        private String after;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Links {
        private String next;
        private String prev;
    }
}