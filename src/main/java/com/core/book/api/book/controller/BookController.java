package com.core.book.api.book.controller;

import com.core.book.api.book.dto.BookInfoDetailDTO;
import com.core.book.api.book.dto.BookResponseDTO;
import com.core.book.api.book.service.BookService;
import com.core.book.common.exception.BadRequestException;
import com.core.book.common.exception.NotFoundException;
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


@Slf4j
@RequiredArgsConstructor
@Tag(name = "Book", description = "Book 관련 API 입니다.")
@RestController
public class BookController {

    private final BookService bookService;

    @Operation(
            summary = "책 검색 API",
            description = "외부 API에 도서 데이터를 요청해 가져옵니다. 반환 값 중 description(책 소개)은 검색 페이지에서 사용되지 않는 데이터 입니다. 다만 타 API에 책 데이터를 넘겨주는 경우에는 같이 사용됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "책 결과 반환 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값에 대한 반환 결과가 없습니다.")
    })
    @GetMapping("/api/v1/book")
    public ResponseEntity<ApiResponse<BookResponseDTO>> book(
            @RequestParam("title") String text,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        log.info("Received text: {}, page: {}, size: {}", text, page, size);

        //예외처리 - 검색어가 입력되지 않았을 경우
        if(text.isEmpty()) {
            throw new BadRequestException(ErrorStatus.VALIDATION_REQUEST_MISSING_EXCEPTION.getMessage());
        }

        BookResponseDTO bookList = bookService.bookSearch(text, page, size);

        return ApiResponse.success(SuccessStatus.BOOK_SEARCH_SUCCESS, bookList);
    }

    @Operation(
            summary = "책 정보 자세히보기 API",
            description = "책 정보 자세히보기 페이지에 표시되는 데이터를 반환합니다. (데이터: 책 정보, 전체 평점, 전체 태그(Best 5), 리뷰 목록)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "책 결과 반환 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "해당 도서의 검색 결과가 없습니다.")
    })
    @GetMapping("/api/v1/book/info")
    public ResponseEntity<ApiResponse<BookInfoDetailDTO>> bookInfo(
            @RequestParam("isbn") String isbn){

        BookInfoDetailDTO bookInfoDetail = bookService.bookInfo(isbn);

        if(bookInfoDetail == null){
            throw new NotFoundException(ErrorStatus.BOOK_SEARCH_NOTFOUND_EXCEPTION.getMessage());
        }

        return ApiResponse.success(SuccessStatus.BOOK_SEARCH_SUCCESS, bookInfoDetail);
    }
}