package com.core.book.api.article.dto;

import com.core.book.api.article.entity.ArticleType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewArticleListDTO {
    private final Long articleId;
    private final Long memberId;
    private final String profileImage;
    private final String nickname;
    private final String content;
    private final long likeCnt;
    private final long commentCnt;
    private final long quoCnt;
    private final String bookImage;
    private final String title;
    private final String author;
    private final ArticleType articleType;

    @Builder
    public ReviewArticleListDTO(Long articleId, Long memberId, String profileImage, String nickname, String content,
                                long likeCnt, long commentCnt, long quoCnt, String bookImage, String title, String author, ArticleType articleType) {
        this.articleId = articleId;
        this.memberId = memberId;
        this.profileImage = profileImage;
        this.nickname = nickname;
        this.content = content;
        this.likeCnt = likeCnt;
        this.commentCnt = commentCnt;
        this.quoCnt = quoCnt;
        this.bookImage = bookImage;
        this.title = title;
        this.author = author;
        this.articleType = articleType;
    }
}
