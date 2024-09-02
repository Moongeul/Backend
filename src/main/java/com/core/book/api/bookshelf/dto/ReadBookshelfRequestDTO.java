package com.core.book.api.bookshelf.dto;

import com.core.book.api.book.dto.BookDto;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReadBookshelfRequestDTO {

    private BookDto bookDto;
    private ReadBooksDTO readBooksDTO;
}
