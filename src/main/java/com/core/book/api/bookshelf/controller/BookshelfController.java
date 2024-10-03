package com.core.book.api.bookshelf.controller;

import com.core.book.api.bookshelf.dto.*;
import com.core.book.api.bookshelf.service.BookShelfService;
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
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Bookshelf", description = "Bookshelf 관련 API 입니다.")
@RestController
public class BookshelfController {

    private final BookShelfService bookShelfService;

    /*
     *
     * 책장 '조회' API
     *
     */

    @Operation(
            summary = "'읽은 책' 책장 전체 조회 API",
            description = "'읽은 책' 책장에 불러올 전체 데이터를 조회합니다.\n" +
                    "filter 는 필터 번호로 책장 필터링에 사용됩니다. (1: 전체보기(최신순), 2: 오래된 순, 3: 평점 높은 순, 4: 평점 낮은 순)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "책장 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값이 입력되지 않았습니다.")
    })
    @GetMapping("/api/v1/bookshelf/read")
    public ResponseEntity<ApiResponse<ReadBookshelfResponseDTO>> showReadBookshelf(
            @RequestParam("member-id") Long memberId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "filter", defaultValue = "1") int filterNum){

        ReadBookshelfResponseDTO readBookshelfData = bookShelfService.showReadBooks(memberId, page, size, filterNum);
        log.info("readBookshelfData: {}", readBookshelfData.toString());

        return ApiResponse.success(SuccessStatus.GET_BOOKSHELF_SUCCESS, readBookshelfData);
    }

    @Operation(
            summary = "'읽고 싶은 책' 책장 전체 조회 API",
            description = "'읽고 싶은 책' 책장에 불러올 전체 데이터를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "책장 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값이 입력되지 않았습니다.")
    })
    @GetMapping("/api/v1/bookshelf/wish")
    public ResponseEntity<ApiResponse<WishBookshelfResponseDTO>> showWishBookshelf(
            @RequestParam("member-id") Long memberId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){

        WishBookshelfResponseDTO wishBookshelfData = bookShelfService.showWishBooks(memberId, page, size);
        log.info(wishBookshelfData.toString());

        return ApiResponse.success(SuccessStatus.GET_BOOKSHELF_SUCCESS, wishBookshelfData);
    }

    /*
     *
     * 책장 '상세 조회' API
     *
     */

    @Operation(
            summary = "'읽은 책' 책장 상세 조회 API",
            description = "'읽은 책' 책장에서 선택한 책의 상세 정보를 조회합니다. (읽은 책 상세 정보 : 날짜(required)/평점/태그/한줄평)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "책장 상세 정보 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 책장에서 선택된 도서를 찾을 수 없습니다.")
    })
    @GetMapping("/api/v1/bookshelf/read/{id}")
    public ResponseEntity<ApiResponse<ReadBooksDTO>> showReadBookshelfDetails(@PathVariable Long id){

        ReadBooksDTO showed = bookShelfService.showReadBooksDetails(id);
        log.info(showed.toString());
        return ApiResponse.success(SuccessStatus.GET_BOOKSHELF_INFO_SUCCESS, showed);
    }

    @Operation(
            summary = "'읽고 싶은 책' 책장 상세 조회 API",
            description = "'읽고 싶은 책' 책장에서 선택한 책의 상세 정보를 조회합니다. (읽고 싶은 책 상세 정보 : 읽고 싶은 이유)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "책장 상세 정보 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 책장에서 선택된 도서를 찾을 수 없습니다.")
    })
    @GetMapping("/api/v1/bookshelf/wish/{id}")
    public ResponseEntity<ApiResponse<WishBooksDTO>> showWishBookshelfDetails(@PathVariable Long id){

        WishBooksDTO showed = bookShelfService.showWishBooksDetails(id);
        log.info(showed.toString());
        return ApiResponse.success(SuccessStatus.GET_BOOKSHELF_INFO_SUCCESS, showed);
    }

    /*
     *
     * 책장 '등록' API
     *
     */

    @Operation(
            summary = "'읽은 책' 책장 등록 API",
            description = "'읽은 책' 책장에 선택한 책을 상세 정보와 함께 등록합니다. (읽은 책 상세 정보 : 날짜(required)/평점/태그/한줄평)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "책장 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없습니다.")
    })
    @PostMapping("/api/v1/bookshelf/read")
    public ResponseEntity<ApiResponse<Void>> createReadBookshelf(@RequestBody ReadBookshelfRequestDTO readBookshelfDTO){

        // 예외처리 : 등록 날짜가 / 등록하는 유저가 정상적으로 입력되지 않은 경우 등록 불가
        if(readBookshelfDTO.getReadBooks().getReadDate() == null){
            throw new BadRequestException(ErrorStatus.MISSING_BOOKSHELF_DATE.getMessage());
        }
        if(readBookshelfDTO.getReadBooks().getMemberId() == null){
            throw new BadRequestException(ErrorStatus.MISSING_BOOKSHELF_MEMBER.getMessage());
        }

        // 책 저장 - 날짜/평점/태그/한줄평 입력 후 저장
        bookShelfService.createReadBookshelf(readBookshelfDTO);

        return ApiResponse.success_only(SuccessStatus.CREATE_BOOKSHELF_SUCCESS);
    }

    @Operation(
            summary = "'읽고 싶은 책' 책장 등록 API",
            description = "'읽고 싶은 책' 책장에 선택한 책을 상세 정보와 함께 등록합니다. (읽고 싶은 책 상세 정보 : 읽고 싶은 이유)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "책장 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없습니다.")
    })
    @PostMapping("/api/v1/bookshelf/wish")
    public ResponseEntity<ApiResponse<Void>> createWishBookshelf(@RequestBody WishBookshelfRequestDTO wishBookshelfDTO){

        // 예외처리 : 등록하는 유저가 입력되지 정상적으로 않은 경우
        if(wishBookshelfDTO.getWishBooks().getMemberId() == null){
            throw new BadRequestException(ErrorStatus.MISSING_BOOKSHELF_MEMBER.getMessage());
        }

        // 책 저장 - 날짜/평점/태그/한줄평 입력 후 저장
        bookShelfService.createWishBookshelf(wishBookshelfDTO);

        return ApiResponse.success_only(SuccessStatus.CREATE_BOOKSHELF_SUCCESS);
    }

    /*
     *
     * 책장 '상세 정보 수정' API
     *
     */

    @Operation(
            summary = "'읽은 책' 상세 정보 수정 API",
            description = "'읽은 책' 책장에서 선택한 책의 상세 정보를 수정합니다. (읽은 책 상세 정보 : 날짜(required)/평점/태그/한줄평)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "책장 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 책장에서 선택된 도서를 찾을 수 없습니다.")
    })
    @PatchMapping("/api/v1/bookshelf/read/{id}")
    public ResponseEntity<ApiResponse<Void>> updateReadBookshelf(@RequestBody ReadBooksDTO readBooksDTO, @PathVariable Long id){

        // 예외 처리 : 등록 날짜가 입력되지 않은 경우
        if(readBooksDTO.getReadDate() == null){
            throw new NotFoundException(ErrorStatus.MISSING_BOOKSHELF_DATE.getMessage());
        }

        bookShelfService.updateReadBookshelf(readBooksDTO, id);

        return ApiResponse.success_only(SuccessStatus.UPDATE_BOOKSHELF_INFO_SUCCESS);
    }

    @Operation(
            summary = "'읽고 싶은 책' 상세 정보 수정 API",
            description = "'읽고 싶은 책' 책장에서 선택한 책의 상세 정보를 수정합니다. (읽고 싶은 책 상세 정보 : 읽고 싶은 이유)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "책장 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 책장에서 선택된 도서를 찾을 수 없습니다.")
    })
    @PatchMapping("/api/v1/bookshelf/wish/{id}")
    public ResponseEntity<ApiResponse<Void>> updateWishBookshelf(@RequestBody WishBooksDTO wishBooksDTO, @PathVariable Long id){

        bookShelfService.updateWishBookshelf(wishBooksDTO, id);

        return ApiResponse.success_only(SuccessStatus.UPDATE_BOOKSHELF_INFO_SUCCESS);
    }

    /*
     *
     * 책장 '삭제' API
     *
     */

    @Operation(
            summary = "'읽은 책' 삭제 API",
            description = "'읽은 책' 책장에서 선택한 책을 삭제합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "책장 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 책장에서 선택된 도서를 찾을 수 없습니다.")
    })
    @DeleteMapping("/api/v1/bookshelf/read/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReadBookshelf(@PathVariable Long id){

        bookShelfService.deleteReadBookshelf(id);

        return ApiResponse.success_only(SuccessStatus.DELETE_BOOKSHELF_SUCCESS);
    }

    @Operation(
            summary = "'읽고 싶은 책' 삭제 API",
            description = "'읽고 싶은 책' 책장에서 선택한 책을 삭제합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "책장 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 책장에서 선택된 도서를 찾을 수 없습니다.")
    })
    @DeleteMapping("/api/v1/bookshelf/wish/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWishBookshelf(@PathVariable Long id){

        bookShelfService.deleteWishBookshelf(id);

        return ApiResponse.success_only(SuccessStatus.DELETE_BOOKSHELF_SUCCESS);
    }

    /*
     *
     * 책장 - 읽고 싶은 책 -> 읽은 책 '이동' API
     *
     */

    @Operation(
            summary = "책장 이동 API",
            description = "'읽고 싶은 책' 책장에서 '책 읽음' 처리한 책을 '읽은 책' 책장으로 이동시킵니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "책장 이동 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 책장에서 선택된 도서를 찾을 수 없습니다.")
    })
    @PostMapping("/api/v1/bookshelf/shift/{id}")
    public ResponseEntity<ApiResponse<Void>> shiftBookshelf(@RequestBody ReadBooksDTO readBooksDTO, @PathVariable Long id){

        bookShelfService.shiftBookshelf(readBooksDTO, id);

        return ApiResponse.success_only(SuccessStatus.SHIFT_BOOKSHELF_SUCCESS);
    }

}
