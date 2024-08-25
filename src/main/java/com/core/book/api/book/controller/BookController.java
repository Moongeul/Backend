package com.core.book.api.book.controller;

import com.core.book.api.book.entity.Book;
import com.core.book.api.book.service.BookService;
import com.core.book.common.response.ApiResponse;
import com.core.book.common.response.ErrorStatus;
import com.core.book.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Book", description = "Book 관련 API 입니다.")
@RestController
public class BookController {

    @Autowired
    private BookService bookService;

    @Operation(
            summary = "도서 데이터 요청 및 저장 API",
            description = "도서 데이터를 외부 API에 요청해 가져오고 이를 DB에 저장합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "책 결과 반환 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값에 대한 반환 결과가 없습니다.")
    })
    @GetMapping("/api/v1/book")
    public ApiResponse<Iterable<Book>> book(@RequestParam("title") String text) {
        log.info("Received text: {}", text);
        if(text.isEmpty()){
            ApiResponse.fail(ErrorStatus.VALIDATION_REQUEST_MISSING_EXCEPTION.getStatusCode(), ErrorStatus.VALIDATION_REQUEST_MISSING_EXCEPTION.getMessage());
        }

        Iterable<Book> book = bookService.book(text);

        return ApiResponse.success(SuccessStatus.BOOK_SEARCH_SUCCESS, book);
    }
}