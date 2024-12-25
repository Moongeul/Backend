package com.core.book.api.article.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PhraseArticleContentDetailDTO {

    private String content;         // 구절에 대한 전체 설명
    private int pageNum;            // 페이지 번호
    private String phraseContent;   // 인상깊은 구절

    // 책 정보
    private String isbn;
    private String title;
    private String author;
    private String bookImage;

    @Builder
    public PhraseArticleContentDetailDTO(String content,
                                         int pageNum,
                                         String phraseContent,
                                         String isbn,
                                         String title,
                                         String author,
                                         String bookImage) {
        this.content = content;
        this.pageNum = pageNum;
        this.phraseContent = phraseContent;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.bookImage = bookImage;
    }
}
