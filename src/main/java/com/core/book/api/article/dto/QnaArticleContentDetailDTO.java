package com.core.book.api.article.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QnaArticleContentDetailDTO {

    private Long contentId;
    private String content;         // 질문 내용

    @Builder
    public QnaArticleContentDetailDTO(Long id, String content) {
        this.contentId = id;
        this.content = content;
    }
}
