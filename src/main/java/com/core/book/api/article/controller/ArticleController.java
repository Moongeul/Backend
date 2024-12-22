package com.core.book.api.article.controller;

import com.core.book.api.article.service.ArticleService;
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
public class ArticleController {

    private final MemberService memberService;
    private final ArticleService articleService;

    @Operation(
            summary = "게시글 좋아요 토글 API",
            description = "특정 게시글에 좋아요를 누르거나 취소합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "좋아요 토글 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 게시글을 찾을 수 없습니다."),
    })
    @PostMapping("/like/{id}")
    public ResponseEntity<ApiResponse<Void>> toggleLike(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        Long userId = memberService.getUserIdByEmail(userDetails.getUsername());
        articleService.toggleLike(id, userId);
        return ApiResponse.success_only(SuccessStatus.TOGGLE_LIKE_SUCCESS);
    }
}
