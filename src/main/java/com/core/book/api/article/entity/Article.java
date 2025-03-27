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

import java.util.ArrayList;
import java.util.List;

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

    protected long likeCnt; // 좋아요 수
    protected long commentCnt; // 댓글 수
    protected long quoCnt; // 인용 수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @OneToMany(mappedBy = "quotedArticle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuotationArticle> quotationArticles = new ArrayList<>();

    public abstract String getContent();
    public abstract Member getMember();

    public abstract void increaseCommentCount();
    public abstract void decreaseCommentCount();

    public abstract void increaseLikeCount();
    public abstract void decreaseLikeCount();

    public abstract void increaseQuoCount();
    public abstract void decreaseQuoCount();
}