package com.core.book.api.book.dto;

import com.core.book.api.book.entity.Book;
import com.core.book.api.book.entity.BookTag;
import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class BookDTO {

    private Long id;

    private String title;
    private String link;
    private String image;
    private String author;
    private String discount;
    private String publisher;
    private String isbn;
    private String description;
    private String pubdate;

    private BookTag bookTag = new BookTag();

    public Book toEntity() {
        return new Book(id, title, image, author, publisher, description, pubdate, bookTag);
    }
}