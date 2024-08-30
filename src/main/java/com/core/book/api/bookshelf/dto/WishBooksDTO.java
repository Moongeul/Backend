package com.core.book.api.bookshelf.dto;

import com.core.book.api.book.entity.Book;
import com.core.book.api.bookshelf.entity.WishBooks;
import com.core.book.api.member.entity.Member;
import lombok.Getter;

@Getter
public class WishBooksDTO {

    private String reason;

    private Long memberId; // 회원 ID

    public WishBooks toEntity(Book book, Member member){
        return WishBooks.builder()
                .reason(this.reason)
                .book(book)
                .member(member)
                .build();
    }
}
