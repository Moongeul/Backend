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
@Table(name = "PHRASE_ARTICLE")
public class PhraseArticle extends Article {

    @Builder.Default
    @OneToMany(mappedBy = "phraseArticle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PhraseArticleContent> phraseArticleContents = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    public void addPhraseArticleContent(PhraseArticleContent phraseArticleContent) {
        this.phraseArticleContents.add(phraseArticleContent);
        phraseArticleContent.setPhraseArticle(this);
    }

    // 댓글 수 증가/감소
    @Override
    public PhraseArticle increaseCommentCount() {
        return this.toBuilder()
                .commentCnt(this.getCommentCnt() + 1)
                .build();
    }

    @Override
    public PhraseArticle decreaseCommentCount() {
        return this.toBuilder()
                .commentCnt(this.getCommentCnt() - 1)
                .build();
    }

    @Override
    public String getContent() {
        // 자식(구절) 리스트가 없으면 빈 객체
        if (phraseArticleContents.isEmpty()) {
            return "{}";
        }

        // 첫 번째 구절만 추출
        PhraseArticleContent first = phraseArticleContents.get(0);

        Integer pageNum      = first.getPageNum();       // 페이지 번호
        String phraseText    = first.getPhraseContent(); // 인상깊은 구절
        String Content = first.getContent();       // 구절에 대한 전체 설명

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"pageNum\":").append(pageNum).append(",");
        sb.append("\"phraseContent\":\"").append(escape(phraseText)).append("\",");
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
