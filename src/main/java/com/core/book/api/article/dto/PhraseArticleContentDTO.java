package com.core.book.api.article.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PhraseArticleContentDTO {

    private String isbn;          // 구절이 참조할 책의 ISBN
    private String content;       // 구절에 대한 전체 설명
    private int pageNum;          // 페이지 번호
    private String phraseContent; // 인상깊은 구절

    @Builder
    public PhraseArticleContentDTO(String isbn, String content, int pageNum, String phraseContent) {
        this.isbn = isbn;
        this.content = content;
        this.pageNum = pageNum;
        this.phraseContent = phraseContent;
    }
}
