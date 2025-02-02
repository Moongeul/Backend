package com.core.book.api.book.dto;

import com.core.book.api.article.entity.ReviewArticle;
import com.core.book.api.book.constant.BookTag;
import com.core.book.api.book.entity.Book;
import com.core.book.api.book.entity.UserBookTag;
import com.core.book.api.bookshelf.entity.ReadBooks;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserBookTagDTO {

    private int tagId;
    private String tag;

    public UserBookTag toEntity(Book book, BookTag tag, ReadBooks readBooks, ReviewArticle reviewArticle) {
        return UserBookTag.builder()
                .book(book)
                .tag(tag.getId())
                .readBooks(readBooks)
                .reviewArticle(reviewArticle)
                .build();
    }

    // 태그 수정
    public UserBookTag update(UserBookTag userBookTag, BookTag tagEnum){
        return userBookTag.toBuilder()
                    .tag(tagEnum.getId())
                    .build();
    }
}
