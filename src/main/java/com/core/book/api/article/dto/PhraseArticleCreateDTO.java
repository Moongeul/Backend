package com.core.book.api.article.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PhraseArticleCreateDTO {

    private String isbn;
    private int pageNum;
    private final String content; // 구절에 대한 자세한 설명
    private final String phraseContent; // 인용 할 구절

    @Builder
    public PhraseArticleCreateDTO(String isbn, int pageNum, String content, String phraseContent) {
        this.isbn = isbn;
        this.pageNum = pageNum;
        this.content = content;
        this.phraseContent = phraseContent;
    }
}
