package com.core.book.api.book.entity;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long book_id;

    private String title;

    private String link;

    private String book_image;

    private String author;

    private String discount;

    private String publisher;

    private String isbn;

    @Column(length = 5000)
    private String description;

    private String pubdate;

//    @Column
//    private String category;
}
