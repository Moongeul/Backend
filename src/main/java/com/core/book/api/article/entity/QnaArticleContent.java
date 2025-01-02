package com.core.book.api.article.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(name = "QNA_ARTICLE_CONTENT")
public class QnaArticleContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;         // 질문 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qna_article_id")
    private QnaArticle qnaArticle;

    // QnaArticle - QnaArticleContent JPA 양방향 매핑 시 꼭 필요한 연관관계 동기화 로직
    public void setQnaArticle(QnaArticle qnaArticle) {
        this.qnaArticle = qnaArticle;
    }
}
