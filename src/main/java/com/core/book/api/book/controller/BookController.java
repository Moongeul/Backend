package com.core.book.api.book.controller;

import com.core.book.api.book.service.BookService;
import com.core.book.common.exception.BadRequestException;
import com.core.book.common.response.ApiResponse;
import com.core.book.common.response.ErrorStatus;
import com.core.book.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Book", description = "Book 관련 API 입니다.")
@RestController
public class BookController {

    private final BookService bookService;

    @Operation(
            summary = "도서 데이터 요청 API",
            description = "외부 API에 도서 데이터를 요청해 가져옵니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "책 결과 반환 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값에 대한 반환 결과가 없습니다.")
    })
    @GetMapping("/api/v1/book")
    public ResponseEntity<ApiResponse<Map<String, Object>>> book(
            @RequestParam("title") String text,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        log.info("Received text: {}, page: {}, size: {}", text, page, size);

        //예외처리 - 검색어가 입력되지 않았을 경우
        if(text.isEmpty()) {
            throw new BadRequestException(ErrorStatus.VALIDATION_REQUEST_MISSING_EXCEPTION.getMessage());
        }

        Map<String, Object> responseMap = bookService.book(text, page, size);

        return ApiResponse.success(SuccessStatus.BOOK_SEARCH_SUCCESS, responseMap);
    }
}