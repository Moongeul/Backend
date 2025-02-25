package com.core.book.api.article.controller;

import com.core.book.api.article.service.ArticleDeleteService;
import com.core.book.api.member.service.MemberService;
import com.core.book.common.response.ApiResponse;
import com.core.book.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Article", description = "게시글 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/article")
public class ArticleDeleteController {

    private final ArticleDeleteService articleDeleteService;
    private final MemberService memberService;

    @Operation(
            summary = "감상평 게시글 삭제 API",
            description = "감상평 게시글을 삭제합니다. (TYPE : REVIEW)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "게시글 작성자와 삭제 요청자가 다릅니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없습니다.")
    })
    @DeleteMapping("/review/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReviewArticle(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = memberService.getUserIdByEmail(userDetails.getUsername());
        articleDeleteService.deleteReviewArticle(id, userId);

        return ApiResponse.success_only(SuccessStatus.DELETE_ARTICLE_SUCCESS);
    }

    @Operation(
            summary = "인상깊은구절 게시글 삭제 API",
            description = "인상깊은구절 게시글을 삭제합니다. (TYPE : PHRASE)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "게시글 작성자와 삭제 요청자가 다릅니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없습니다.")
    })
    @DeleteMapping("/phrase/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePhraseArticle(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = memberService.getUserIdByEmail(userDetails.getUsername());
        articleDeleteService.deletePhraseArticle(id, userId);

        return ApiResponse.success_only(SuccessStatus.DELETE_ARTICLE_SUCCESS);
    }

    @Operation(
            summary = "QnA 게시글 삭제 API",
            description = "QnA 게시글을 삭제합니다. (TYPE : QNA)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "게시글 작성자와 삭제 요청자가 다릅니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없습니다.")
    })
    @DeleteMapping("/qna/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQnaArticle(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = memberService.getUserIdByEmail(userDetails.getUsername());
        articleDeleteService.deleteQnaArticle(id, userId);

        return ApiResponse.success_only(SuccessStatus.DELETE_ARTICLE_SUCCESS);
    }

    @Operation(
            summary = "인용 게시글 삭제 API",
            description = "인용 게시글을 삭제합니다. (TYPE : QUOTATION)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "게시글 작성자와 삭제 요청자가 다릅니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없습니다.")
    })
    @DeleteMapping("/quotation/{QuoatationArticleId}")
    public ResponseEntity<ApiResponse<Void>> deleteQuotationArticle(
            @PathVariable Long QuoatationArticleId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = memberService.getUserIdByEmail(userDetails.getUsername());
        articleDeleteService.deleteQuotationArticle(QuoatationArticleId, userId);

        return ApiResponse.success_only(SuccessStatus.DELETE_ARTICLE_SUCCESS);
    }

}
