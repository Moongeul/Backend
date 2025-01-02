package com.core.book.api.article.entity;

import com.core.book.api.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import java.util.ArrayList;
import java.util.List;

@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "QNA_ARTICLE")
public class QnaArticle extends Article{

    @Builder.Default
    @OneToMany(mappedBy = "qnaArticle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QnaArticleContent> qnaArticleContents = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    public void addQnaArticleContent(QnaArticleContent qnaArticleContent) {
        this.qnaArticleContents.add(qnaArticleContent);
        qnaArticleContent.setQnaArticle(this);
    }

    // 댓글 수 증가
    @Override
    public QnaArticle increaseCommentCount() {
        return this.toBuilder()
                .commentCnt(this.getCommentCnt() + 1)
                .build();
    }

    // 댓글 수 감소
    @Override
    public QnaArticle decreaseCommentCount() {
        return this.toBuilder()
                .commentCnt(this.getCommentCnt() - 1)
                .build();
    }

    // 좋아요 증가
    public Article increaseLikeCount() {
        return this.toBuilder()
                .likeCnt(this.getLikeCnt() + 1)
                .build();
    }

    // 좋아요 감소
    public Article decreaseLikeCount() {
        return this.toBuilder()
                .likeCnt(this.getLikeCnt() - 1)
                .build();
    }

    @Override
    public String getContent() {
        // 질문 리스트가 없으면 빈 객체
        if (qnaArticleContents.isEmpty()) {
            return "{}";
        }

        // 첫 번째 구절만 추출
        QnaArticleContent first = qnaArticleContents.get(0);

        String Content = first.getContent();       // 질문 내용

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"content\":\"").append(escape(Content)).append("\"");
        sb.append("}");
        return sb.toString();
    }

    // 문자열 중간의 " 를 간단히 변환하기 위한  메서드
    private String escape(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"");
    }

    @Override
    public Member getMember() {
        return this.member;
    }
}
