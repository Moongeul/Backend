package com.core.book.api.bookshelf.dto;

import com.core.book.api.book.dto.BookInfoDTO;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WishBookshelfRequestDTO {

    private BookInfoDTO bookInfo;
    private WishBooksDTO wishBooks;
}
