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
            summary = "처음 사용자용 마케팅 정보 동의 등록 API",
            description = "처음 사용자 추가 정보 등록페이지 에서 마케팅 동의 여부를 확인 후 동의 여부 등록합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "책 결과 반환 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "최소 한개 이상의 USERTAG가 발송되지 않았습니다.")
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