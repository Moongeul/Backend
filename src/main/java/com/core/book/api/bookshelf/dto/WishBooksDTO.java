package com.core.book.api.bookshelf.dto;

import com.core.book.api.book.entity.Book;
import com.core.book.api.bookshelf.entity.WishBooks;
import com.core.book.api.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WishBooksDTO {

    private String reason; // 읽고 싶은 이유

    private Long memberId; // 회원 ID

    public WishBooks toEntity(Book book, Member member){
        return WishBooks.builder()
                .reason(this.reason)
                .book(book)
                .member(member)
                .build();
    }
}
