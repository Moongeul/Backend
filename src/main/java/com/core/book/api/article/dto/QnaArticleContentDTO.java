package com.core.book.api.article.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QnaArticleContentDTO {

    private String content;       // 질문 내용

    @Builder
    public QnaArticleContentDTO(String content) {
        this.content = content;
    }
}
