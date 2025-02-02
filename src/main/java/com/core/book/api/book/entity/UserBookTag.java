package com.core.book.api.book.entity;

import com.core.book.api.article.entity.ReviewArticle;
import com.core.book.api.bookshelf.entity.ReadBooks;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class UserBookTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book; //책 - isbn

    private int tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "readbooks_id")
    private ReadBooks readBooks; //책장

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_article_id")
    private ReviewArticle reviewArticle; //감상평 게시글
}
