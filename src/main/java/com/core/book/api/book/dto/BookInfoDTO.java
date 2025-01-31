package com.core.book.api.book.dto;

import com.core.book.api.book.entity.Book;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BookInfoDTO {

    private String isbn; // isbn
    private String image; // 책 이미지
    private String title; // 책 제목
    private String author; // 저자
    private String publisher; // 출판사
    private String pubdate; // 출판 날짜
    private String description; // 책 소개

    public Book toEntity() {

        return Book.builder()
                .isbn(isbn)
                .title(title)
                .bookImage(image)
                .author(author)
                .publisher(publisher)
                .description(description)
                .pubdate(pubdate)
                .build();
    }
}
