package com.core.book.api.article.entity;

import com.core.book.api.book.entity.Book;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(name = "PHRASE_ARTICLE_CONTENT")
public class PhraseArticleContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;         // 구절에 대한 전체 설명

    private int pageNum;            // 인상깊은 페이지 번호

    @Column(columnDefinition = "TEXT")
    private String phraseContent;   // 인상깊은 구절

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phrase_article_id")
    private PhraseArticle phraseArticle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    // PhraseArticle - PhraseArticleContent JPA 양방향 매핑 시 꼭 필요한 연관관계 동기화 로직
    public void setPhraseArticle(PhraseArticle phraseArticle) {
        this.phraseArticle = phraseArticle;
    }
}
