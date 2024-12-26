package com.core.book.api.article.entity;

import com.core.book.api.book.entity.Book;
import com.core.book.api.member.entity.Member;
import com.core.book.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "article_type")
public abstract class Article extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ArticleType type;

    private long likeCnt; // 좋아요 수
    private long commentCnt; // 댓글 수
    private long quoCnt; // 인용 수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    public abstract String getContent();
    public abstract Member getMember();

    public abstract Article increaseCommentCount();
    public abstract Article decreaseCommentCount();

    public abstract Article increaseLikeCount();
    public abstract Article decreaseLikeCount();
}