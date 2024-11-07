package com.core.book.api.bookshelf.dto;

import com.core.book.api.book.entity.Book;
import com.core.book.api.bookshelf.entity.WishBooks;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WishBooksDTO {

    private String reason; // 읽고 싶은 이유

    public WishBooks toEntity(Book book){
        return WishBooks.builder()
                .reason(this.reason)
                .book(book)
                .build();
    }
}
