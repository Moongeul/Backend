package com.core.book.api.member.controller;

import com.core.book.api.article.dto.ArticleListResponseDTO;
import com.core.book.api.member.service.MyPageService;
import com.core.book.common.response.ApiResponse;
import com.core.book.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MyPage", description = "마이페이지 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mypage")
public class MyPageController {

    private final MyPageService myPageService;

    @Operation(summary = "타인 사용자가 작성한 게시글 목록 조회 API",
            description = "타인 사용자가 작성한 Review, Phrase, QnA 게시글을 조회합니다. 단, 해당 사용자의 게시글 공개(contentOpen) 설정이 true일 경우에만 조회됩니다.")
    @GetMapping("/{userId}/articles")
    public ResponseEntity<ApiResponse<ArticleListResponseDTO>> getUserArticles(
            @PathVariable Long userId,
            @RequestParam int page,
            @RequestParam int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        ArticleListResponseDTO response = myPageService.getUserArticles(userId, page, size, userDetails);
        return ApiResponse.success(SuccessStatus.GET_ARTICLE_LIST_SUCCESS, response);
    }

    @Operation(summary = "타인 사용자가 좋아요한 게시글 목록 조회 API",
            description = "타인 사용자가 좋아요한 Review, Phrase, QnA 게시글들을 조회합니다. 단, 해당 사용자의 좋아요 공개(likeOpen) 설정이 true일 경우에만 조회됩니다.")
    @GetMapping("/{userId}/liked-articles")
    public ResponseEntity<ApiResponse<ArticleListResponseDTO>> getUserLikedArticles(
            @PathVariable Long userId,
            @RequestParam int page,
            @RequestParam int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        ArticleListResponseDTO response = myPageService.getUserLikedArticles(userId, page, size, userDetails);
        return ApiResponse.success(SuccessStatus.GET_ARTICLE_LIST_SUCCESS, response);
    }

    @Operation(summary = "타인 사용자가 댓글을 남긴 게시글 목록 조회 API",
            description = "타인 사용자가 댓글을 남긴 Review, Phrase, QnA 게시글들을 조회합니다. 단, 해당 사용자의 댓글 공개(commentOpen) 설정이 true일 경우에만 조회됩니다.")
    @GetMapping("/{userId}/commented-articles")
    public ResponseEntity<ApiResponse<ArticleListResponseDTO>> getUserCommentedArticles(
            @PathVariable Long userId,
            @RequestParam int page,
            @RequestParam int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        ArticleListResponseDTO response = myPageService.getUserCommentedArticles(userId, page, size, userDetails);
        return ApiResponse.success(SuccessStatus.GET_ARTICLE_LIST_SUCCESS, response);
    }
}
