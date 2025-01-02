package com.core.book.api.article.dto;

import com.core.book.api.article.entity.ArticleType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ArticleListDTO {
    private final Long articleId;
    private final Long memberId;
    private final String profileImage;
    private final String nickname;
    private final String content;
    private final long likeCnt;    // 공통 필드
    private final long commentCnt; // 공통 필드
    private final long quoCnt;
    private final String bookImage;
    private final String title;
    private final String author;
    private final ArticleType articleType; // 게시글 타입 구분을 위한 필드
    private final String date;
    private final boolean myLike;

    @Builder
    public ArticleListDTO(Long articleId, Long memberId, String profileImage, String nickname, String content, String date,
                          long likeCnt, long commentCnt, long quoCnt, String bookImage, String title, String author, ArticleType articleType, boolean myLike) {
        this.articleId = articleId;
        this.memberId = memberId;
        this.profileImage = profileImage;
        this.nickname = nickname;
        this.content = content;
        this.likeCnt = likeCnt;
        this.quoCnt = quoCnt;
        this.commentCnt = commentCnt;
        this.bookImage = bookImage;
        this.title = title;
        this.author = author;
        this.articleType = articleType;
        this.date = date;
        this.myLike = myLike;
    }
}
