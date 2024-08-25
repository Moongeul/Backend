package com.core.book.api.book.entity;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    private String title; // 책 제목
    private String book_image; // 책 이미지
    private String author; // 저자
    private String publisher; // 출판사

    @Column(length = 5000)
    private String description; //책 소개
    private String pubdate; // 출판연도

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "book_tag_id")
    private BookTag bookTag; //책 태그 - entity
}
