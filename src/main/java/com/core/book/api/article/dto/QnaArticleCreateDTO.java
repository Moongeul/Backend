package com.core.book.api.article.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class QnaArticleCreateDTO {

    private String isbn;
    private List<QnaArticleContentDTO> qnaContents;

    @Builder
    public QnaArticleCreateDTO(List<QnaArticleContentDTO> qnaContents, String isbn) {
        this.qnaContents = qnaContents;
        this.isbn = isbn;
    }
}
