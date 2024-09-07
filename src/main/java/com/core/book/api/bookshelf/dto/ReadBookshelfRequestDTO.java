package com.core.book.api.bookshelf.dto;

import com.core.book.api.book.dto.BookDTO;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReadBookshelfRequestDTO {

    private BookDTO bookDTO;
    private ReadBooksDTO readBooksDTO;
}
