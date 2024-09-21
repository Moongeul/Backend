package com.core.book.api.bookshelf.dto;

import com.core.book.api.book.dto.BookDTO;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WishBookshelfRequestDTO {

    private BookDTO book;
    private WishBooksDTO wishBooks;
}
