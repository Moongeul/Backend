package com.core.book.api.article.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ArticleListResponseDTO {
    private final List<ArticleListDTO> articles; // 게시글 리스트
    private final boolean isLast; // 마지막 페이지 여부
    private final int page;       // 현재 페이지 정보

    public ArticleListResponseDTO(List<ArticleListDTO> articles, boolean isLast, int page) {
        this.articles = articles;
        this.isLast = isLast;
        this.page = page;
    }
}
