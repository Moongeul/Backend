package com.core.book.api.article.controller;

import com.core.book.api.article.dto.ReviewArticleDetailDTO;
import com.core.book.api.article.dto.ReviewArticleListResponseDTO;
import com.core.book.api.article.service.ArticleViewService;
import com.core.book.common.response.ApiResponse;
import com.core.book.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Article", description = "게시글 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/article")
public class ArticleViewController {

    private final ArticleViewService articleViewService;

    @Operation(
            summary = "감상평 게시글 상세 조회 API",
            description = "감상평 게시글의 상세 정보를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없습니다.")
    })
    @GetMapping("/review/{id}")
    public ResponseEntity<ApiResponse<ReviewArticleDetailDTO>> getReviewArticleDetail(@PathVariable Long id) {
        ReviewArticleDetailDTO reviewArticleDetailDTO = articleViewService.getReviewArticleDetail(id);
        return ApiResponse.success(SuccessStatus.GET_ARTICLE_SUCCESS, reviewArticleDetailDTO);
    }

    @Operation(
            summary = "감상평 게시글 전체 조회 API",
            description = "감상평 게시글의 목록을 조회합니다. / hasNext : true 일 경우 다음 데이터가 있다는 의미입니다. / 페이지는 0부터 시작합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공")
    })
    @GetMapping("/review/all")
    public ResponseEntity<ApiResponse<ReviewArticleListResponseDTO>> getAllReviewArticles(
            @RequestParam int page,
            @RequestParam int size
    ) {
        ReviewArticleListResponseDTO reviewArticleListResponseDTO = articleViewService.getAllReviewArticles(page, size);
        return ApiResponse.success(SuccessStatus.GET_ARTICLE_LIST_SUCCESS, reviewArticleListResponseDTO);
    }
}
