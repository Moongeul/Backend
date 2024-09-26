package com.core.book.api.article.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ReviewArticleListResponseDTO {
    private final List<ReviewArticleListDTO> articles;
    private final boolean hasNext;

    public ReviewArticleListResponseDTO(List<ReviewArticleListDTO> articles, boolean hasNext) {
        this.articles = articles;
        this.hasNext = hasNext;
    }
}
