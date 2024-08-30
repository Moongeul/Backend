package com.core.book.api.bookshelf.dto;

import com.core.book.api.book.dto.BookDto;
import lombok.Getter;

@Getter
public class ReadBookshelfDTO {

    private BookDto bookDto;
    private ReadBooksDTO readBooksDTO;
}
