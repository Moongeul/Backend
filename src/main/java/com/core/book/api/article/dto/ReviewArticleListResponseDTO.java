package com.core.book.api.article.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ReviewArticleListResponseDTO {
    private final List<ReviewArticleListDTO> articles;
    private final boolean isLast;

    public ReviewArticleListResponseDTO(List<ReviewArticleListDTO> articles, boolean isLast) {
        this.articles = articles;
        this.isLast = isLast;
    }
}
