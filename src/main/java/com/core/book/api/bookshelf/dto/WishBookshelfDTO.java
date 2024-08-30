package com.core.book.api.bookshelf.dto;

import com.core.book.api.book.dto.BookDto;
import lombok.Getter;

@Getter
public class WishBookshelfDTO {

    private BookDto bookDto;
    private WishBooksDTO wishBooksDTO;
}
