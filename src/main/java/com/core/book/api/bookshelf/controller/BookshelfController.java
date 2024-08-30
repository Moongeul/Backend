package com.core.book.api.bookshelf.controller;

import com.core.book.api.bookshelf.dto.ReadBookshelfDTO;
import com.core.book.api.bookshelf.dto.WishBookshelfDTO;
import com.core.book.api.bookshelf.service.BookShelfService;
import com.core.book.common.exception.BadRequestException;
import com.core.book.common.response.ApiResponse;
import com.core.book.common.response.ErrorStatus;
import com.core.book.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "BOOKSHELF", description = "BOOKSHELF 관련 API 입니다.")
@RestController
public class BookshelfController {

    @Autowired
    private BookShelfService bookShelfService;

    @Operation(
            summary = "'읽은 책' 책장 등록 API",
            description = "'읽은 책' 책장에 선택한 책을 추가 정보와 함께 등록합니다. (읽은 책 추가 정보 : 날짜(required)/평점/태그/한줄평)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "책장 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값에 대한 반환 결과가 없습니다.")
    })
    @PostMapping("/api/v1/bookshelf/read")
    public ResponseEntity<ApiResponse<Void>> createReadBookshelf(@RequestBody ReadBookshelfDTO readBookshelfDTO){

        // 예외처리 : body 전체가 / 등록 날짜가 / 등록하는 유저가 입력되지 않은 경우
        if(readBookshelfDTO == null){
            throw new BadRequestException(ErrorStatus.VALIDATION_REQUEST_MISSING_EXCEPTION.getMessage());
        }
        if(readBookshelfDTO.getReadBooksDTO().getReadDate() == null){
            throw new BadRequestException(ErrorStatus.MISSING_BOOKSHELF_DATE.getMessage());
        }
        if(readBookshelfDTO.getReadBooksDTO().getMemberId() == null){
            throw new BadRequestException(ErrorStatus.MISSING_BOOKSHELF_MEMBER.getMessage());
        }

        // 책 저장 - 날짜/평점/태그/한줄평 입력 후 저장
        bookShelfService.createReadBookshelf(readBookshelfDTO);

        return ApiResponse.success_only(SuccessStatus.CREATE_BOOKSHELF_SUCCESS);
    }

    @Operation(
            summary = "'읽고 싶은 책' 책장 등록 API",
            description = "'읽고 싶은 책' 책장에 선택한 책을 추가 정보와 함께 등록합니다. (읽고 싶은 책 추가 정보 : 읽고 싶은 이유)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "책장 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값에 대한 반환 결과가 없습니다.")
    })
    @PostMapping("/api/v1/bookshelf/wish")
    public ResponseEntity<ApiResponse<Void>> createWishBookshelf(@RequestBody WishBookshelfDTO wishBookshelfDTO){

        // 예외처리 : body 전체가 / 등록 날짜가 / 등록하는 유저가 입력되지 않은 경우
        if(wishBookshelfDTO == null){
            throw new BadRequestException(ErrorStatus.VALIDATION_REQUEST_MISSING_EXCEPTION.getMessage());
        }
        if(wishBookshelfDTO.getWishBooksDTO().getMemberId() == null){
            throw new BadRequestException(ErrorStatus.MISSING_BOOKSHELF_MEMBER.getMessage());
        }

        // 책 저장 - 날짜/평점/태그/한줄평 입력 후 저장
        bookShelfService.createWishBookshelf(wishBookshelfDTO);

        return ApiResponse.success_only(SuccessStatus.CREATE_BOOKSHELF_SUCCESS);
    }
}
