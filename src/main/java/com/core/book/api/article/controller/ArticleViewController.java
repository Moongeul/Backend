package com.core.book.api.article.controller;

import com.core.book.api.article.dto.ArticleListResponseDTO;
import com.core.book.api.article.dto.PhraseArticleDetailDTO;
import com.core.book.api.article.dto.QnaArticleDetailDTO;
import com.core.book.api.article.dto.ReviewArticleDetailDTO;
import com.core.book.api.article.service.ArticleViewService;
import com.core.book.common.response.ApiResponse;
import com.core.book.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@Tag(name = "Article", description = "게시글 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/article")
public class ArticleViewController {

    private final ArticleViewService articleViewService;

    @Operation(
            summary = "게시글 전체 조회 API",
            description = "게시글 목록을 조회합니다. / last : true 일 경우 마지막 데이터라는 의미입니다. / 페이지는 0부터 시작합니다. / 게시글전체 조회 타입 : all,  감상평 조회 타입 : review, 인상깊은구절 조회 타입 : phrase, QnA 조회 타입 : qna, 인용 조회 타입 : quotation, 추천해주세요 조회 타입 : recommend"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "게시글 타입이 존재하지 않습니다."),
    })
    @GetMapping("/{articleType}")
    public ResponseEntity<ApiResponse<ArticleListResponseDTO>> getAllArticles(
            @PathVariable String articleType,
            @RequestParam int page,
            @RequestParam int size,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        ArticleListResponseDTO articleListResponseDTO = articleViewService.getAllArticles(articleType, page, size, userDetails);
        return ApiResponse.success(SuccessStatus.GET_ARTICLE_LIST_SUCCESS, articleListResponseDTO);
    }

    @Operation(
            summary = "감상평 게시글 상세 조회 API",
            description = "감상평 게시글의 상세 정보를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없습니다.")
    })
    @GetMapping("/review/{id}")
    public ResponseEntity<ApiResponse<ReviewArticleDetailDTO>> getReviewArticleDetail(@PathVariable Long id,
                                                                                      @AuthenticationPrincipal UserDetails userDetails) {
        ReviewArticleDetailDTO reviewArticleDetailDTO = articleViewService.getReviewArticleDetail(id, userDetails);
        return ApiResponse.success(SuccessStatus.GET_ARTICLE_SUCCESS, reviewArticleDetailDTO);
    }

    @Operation(
            summary = "인상깊은구절 게시글 상세 조회 API",
            description = "인상깊은구절 게시글의 상세 정보를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없습니다.")
    })
    @GetMapping("/phrase/{id}")
    public ResponseEntity<ApiResponse<PhraseArticleDetailDTO>> getPhraseArticleDetail(@PathVariable Long id,
                                                                                      @AuthenticationPrincipal UserDetails userDetails) {
        PhraseArticleDetailDTO phraseArticleDetailDTO = articleViewService.getPhraseArticleDetail(id,userDetails);
        return ApiResponse.success(SuccessStatus.GET_ARTICLE_SUCCESS, phraseArticleDetailDTO);
    }

    @Operation(
            summary = "QnA 게시글 상세 조회 API",
            description = "QnA 게시글의 상세 정보를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없습니다.")
    })
    @GetMapping("/qna/{id}")
    public ResponseEntity<ApiResponse<QnaArticleDetailDTO>> getQnaArticleDetail(@PathVariable Long id,
                                                                                @AuthenticationPrincipal UserDetails userDetails) {
        QnaArticleDetailDTO qnaArticleDetailDTO = articleViewService.getQnaArticleDetail(id,userDetails);
        return ApiResponse.success(SuccessStatus.GET_ARTICLE_SUCCESS, qnaArticleDetailDTO);
    }
}
