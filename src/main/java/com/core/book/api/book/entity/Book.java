package com.core.book.api.book.entity;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@ToString
@Getter
public class Book {

    @Id
    @Column(name = "book_id")
    private String isbn; //isbn - PK

    private String title; // 책 제목

    @Column(name = "book_image")
    private String bookImage; // 책 이미지
    private String author; // 저자
    private String publisher; // 출판사

    @Column(length = 5000)
    private String description; //책 소개
    private String pubdate; // 출판연도

    @Column(name = "rating_average")
    private double ratingAverage; // 평점 (전체 평균)

    @Column(name = "rating_count")
    private int ratingCount; // 평점 개수

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "book_tag_id")
    private BookTag bookTag; //책 태그 - entity
}
