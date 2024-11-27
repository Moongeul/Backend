package com.core.book.api.article.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewArticleCreateDTO {

    private String isbn;
    private final String content;
    private final String oneLineReview;
    private final float rating;
    private final ReviewArticleTagDTO reviewArticleTagDTO;

    @Builder
    public ReviewArticleCreateDTO(String isbn, String content, String oneLineReview, float rating, ReviewArticleTagDTO reviewArticleTagDTO) {
        this.isbn = isbn;
        this.content = content;
        this.oneLineReview = oneLineReview;
        this.rating = rating;
        this.reviewArticleTagDTO = reviewArticleTagDTO;
    }
}
