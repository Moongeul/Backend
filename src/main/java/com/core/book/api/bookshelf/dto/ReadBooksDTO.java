package com.core.book.api.bookshelf.dto;

import com.core.book.api.book.entity.Book;
import com.core.book.api.bookshelf.entity.ReadBooks;
import com.core.book.api.bookshelf.entity.ReadBooksTag;
import com.core.book.api.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReadBooksDTO {

    private LocalDate readDate; // 읽은 날짜
    private double starRating; // 평점
    private String oneLineReview; // 한줄평
    private ReadBooksTagDTO readBooksTag; //태그

    public ReadBooks toEntity(Book book, Member member, ReadBooksTag readBooksTag){
        return ReadBooks.builder()
                .readDate(this.readDate)
                .starRating(this.starRating)
                .oneLineReview(this.oneLineReview)
                .book(book)
                .member(member)
                .readBooksTag(readBooksTag)
                .build();
    }

    @Builder
    @Getter
    public static class ReadBooksTagDTO {

        private String tag1;
        private String tag2;
        private String tag3;
        private String tag4;
        private String tag5;

        public ReadBooksTag toEntity() {
            return ReadBooksTag.builder()
                    .tag1(this.tag1)
                    .tag2(this.tag2)
                    .tag3(this.tag3)
                    .tag4(this.tag4)
                    .tag5(this.tag5)
                    .build();
        }
    }
}
