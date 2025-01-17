package com.core.book.api.bookshelf.dto;

import com.core.book.api.book.entity.Book;
import com.core.book.api.bookshelf.entity.WishBooks;
import com.core.book.api.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WishBooksDTO {

    private String reason; // 읽고 싶은 이유

    public WishBooks toEntity(Book book, Member member){
        return WishBooks.builder()
                .reason(this.reason)
                .book(book)
                .member(member)
                .build();
    }
}
