package com.core.book.api.book.controller;

import com.core.book.api.book.entity.Book;
import com.core.book.api.book.service.BookService;
import com.core.book.common.response.ApiResponse;
import com.core.book.common.response.ErrorStatus;
import com.core.book.common.response.SuccessStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/api/v1/book")
    public ApiResponse<Iterable<Book>> book(@RequestParam("title") String text) {
        log.info("Received text: {}", text);
        Iterable<Book> book = bookService.book(text);

        return (book != null) ?
                ApiResponse.success(SuccessStatus.BOOK_SEARCH_SUCCESS, book) :
                ApiResponse.fail(ErrorStatus.VALIDATION_REQUEST_MISSING_EXCEPTION.getStatusCode(), ErrorStatus.VALIDATION_REQUEST_MISSING_EXCEPTION.getMessage());
    }
}