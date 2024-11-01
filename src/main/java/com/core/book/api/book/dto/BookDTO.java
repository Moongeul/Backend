package com.core.book.api.book.dto;

import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class BookDTO {

    private String isbn;
    private String title;
    private String link;
    private String image;
    private String author;
    private String discount;
    private String publisher;
    private String description;
    private String pubdate;
}