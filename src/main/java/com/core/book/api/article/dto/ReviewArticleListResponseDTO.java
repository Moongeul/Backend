package com.core.book.api.article.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ReviewArticleListResponseDTO {
    private final List<ReviewArticleListDTO> articles;
    private final boolean isLast;
    private final int page;

    public ReviewArticleListResponseDTO(List<ReviewArticleListDTO> articles, boolean isLast, int page) {
        this.articles = articles;
        this.isLast = isLast;
        this.page = page;
    }
}
