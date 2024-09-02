package com.core.book.api.bookshelf.dto;

import com.core.book.api.book.dto.BookDto;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WishBookshelfRequestDTO {

    private BookDto bookDto;
    private WishBooksDTO wishBooksDTO;
}
