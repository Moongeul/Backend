package com.core.book.api.bookshelf.dto;

import com.core.book.api.book.dto.UserBookTagDTO;
import com.core.book.api.book.entity.Book;
import com.core.book.api.bookshelf.entity.ReadBooks;
import com.core.book.api.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReadBooksDTO {

    private LocalDate readDate; // 읽은 날짜
    private double rating; // 평점
    private String oneLineReview; // 한줄평
    private List<UserBookTagDTO> userBookTagList; //태그 (리스트)

    public ReadBooks toEntity(Book book, Member member){
        return ReadBooks.builder()
                .readDate(this.readDate)
                .rating(this.rating)
                .oneLineReview(this.oneLineReview)
                .book(book)
                .member(member)
                .build();
    }
}
