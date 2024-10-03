package com.core.book.api.article.dto;

import com.core.book.api.article.entity.ReviewArticleTag;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewArticleTagDTO {

    private final String tag1;
    private final String tag2;
    private final String tag3;
    private final String tag4;
    private final String tag5;

    @Builder
    public ReviewArticleTagDTO(String tag1, String tag2, String tag3, String tag4, String tag5) {
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.tag3 = tag3;
        this.tag4 = tag4;
        this.tag5 = tag5;
    }

    public ReviewArticleTag toEntity() {
        return ReviewArticleTag.builder()
                .tag1(tag1)
                .tag2(tag2)
                .tag3(tag3)
                .tag4(tag4)
                .tag5(tag5)
                .build();
    }
}
