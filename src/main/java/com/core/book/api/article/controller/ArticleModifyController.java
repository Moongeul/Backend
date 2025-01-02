package com.core.book.api.article.controller;

import com.core.book.api.article.dto.PhraseArticleCreateDTO;
import com.core.book.api.article.dto.QnaArticleCreateDTO;
import com.core.book.api.article.dto.ReviewArticleCreateDTO;
import com.core.book.api.article.service.ArticleModifyService;
import com.core.book.api.member.service.MemberService;
import com.core.book.common.exception.NotFoundException;
import com.core.book.common.response.ApiResponse;
import com.core.book.common.response.ErrorStatus;
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
public class ArticleModifyController {

    private final ArticleModifyService articleModifyService;
    private final MemberService memberService;

    @Operation(
            summary = "감상평 게시글 수정 API",
            description = "감상평 게시글을 수정합니다. (TYPE : REVIEW)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "게시글 작성자와 수정 요청자가 다릅니다.")
    })
    @PutMapping("/review/{id}")
    public ResponseEntity<ApiResponse<Void>> modifyReviewArticle(
            @PathVariable Long id,
            @RequestBody ReviewArticleCreateDTO reviewArticleCreateDTO,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // content 누락시 예외처리
        if (reviewArticleCreateDTO.getContent() == null || reviewArticleCreateDTO.getContent().isEmpty()) {
            throw new NotFoundException(ErrorStatus.VALIDATION_CONTENT_MISSING_EXCEPTION.getMessage());
        }

        Long userId = memberService.getUserIdByEmail(userDetails.getUsername());
        articleModifyService.modifyReviewArticle(id, reviewArticleCreateDTO, userId);

        return ApiResponse.success_only(SuccessStatus.MODIFY_ARTICLE_SUCCESS);
    }

    @Operation(summary = "인상깊은구절 게시글 수정 API", description = "인상깊은구절 게시글을 수정합니다. (TYPE : PHRASE)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "게시글 작성자와 수정 요청자가 다릅니다.")
    })
    @PutMapping("/phrase/{id}")
    public ResponseEntity<ApiResponse<Void>> modifyPhraseArticle(
            @PathVariable Long id,
            @RequestBody PhraseArticleCreateDTO phraseArticleCreateDTO,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        if (phraseArticleCreateDTO.getPhraseContents() == null || phraseArticleCreateDTO.getPhraseContents().isEmpty()) {
            throw new NotFoundException(ErrorStatus.VALIDATION_CONTENT_MISSING_EXCEPTION.getMessage());
        }

        Long userId = memberService.getUserIdByEmail(userDetails.getUsername());
        articleModifyService.modifyPhraseArticle(id, phraseArticleCreateDTO, userId);

        return ApiResponse.success_only(SuccessStatus.MODIFY_ARTICLE_SUCCESS);
    }

    @Operation(summary = "QnA 게시글 수정 API", description = "QnA 게시글을 수정합니다. (TYPE : QNA)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "게시글 작성자와 수정 요청자가 다릅니다.")
    })
    @PutMapping("/qna/{id}")
    public ResponseEntity<ApiResponse<Void>> modifyQnaArticle(
            @PathVariable Long id,
            @RequestBody QnaArticleCreateDTO qnaArticleCreateDTO,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        if (qnaArticleCreateDTO.getQnaContents() == null || qnaArticleCreateDTO.getQnaContents().isEmpty()) {
            throw new NotFoundException(ErrorStatus.VALIDATION_CONTENT_MISSING_EXCEPTION.getMessage());
        }

        Long userId = memberService.getUserIdByEmail(userDetails.getUsername());
        articleModifyService.modifyQnaArticle(id, qnaArticleCreateDTO, userId);

        return ApiResponse.success_only(SuccessStatus.MODIFY_ARTICLE_SUCCESS);
    }
}
