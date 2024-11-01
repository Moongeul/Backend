package com.core.book.api.bookshelf.dto;

import com.core.book.api.book.dto.BookInfoDTO;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReadBookshelfRequestDTO {

    private BookInfoDTO bookInfo;
    private ReadBooksDTO readBooks;
}
