package com.core.book.api.comment.controller;

import com.core.book.api.comment.dto.CommentCreateDTO;
import com.core.book.api.comment.dto.CommentResponseDTO;
import com.core.book.api.comment.service.CommentService;
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

import jakarta.validation.Valid;

import java.util.List;

@Tag(name = "Comment", description = "댓글 관련 API 입니다.")
@RestController
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final MemberService memberService;

    @Operation(
            summary = "게시글 댓글 작성 API",
            description = "게시글에 댓글을 작성합니다 (대댓글은 parentId(부모 댓글 ID)를 넣어야합니다. 부모 댓글로 등록시 null로 해주세요!)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "댓글 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "댓글이 입력되지 않았습니다. / 게시글 ID가 입력되지 않았습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없습니다. / 부모 댓글을 찾을 수 없습니다.")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid CommentCreateDTO commentCreateDTO
    ) {

        // Comment 누락시 예외처리
        if (commentCreateDTO.getComment() == null || commentCreateDTO.getComment().isEmpty()) {
            throw new NotFoundException(ErrorStatus.MISSING_COMMENT.getMessage());
        }

        // 게시글 ID 누락시 예외처리
        if (commentCreateDTO.getArticleId() == null) {
            throw new NotFoundException(ErrorStatus.MISSING_COMMENT_ARTICLEID.getMessage());
        }

        Long userId = memberService.getUserIdByEmail(userDetails.getUsername());
        commentService.createComment(commentCreateDTO, userId);

        return ApiResponse.success_only(SuccessStatus.CREATE_COMMENT_SUCCESS);
    }

    @Operation(
            summary = "게시글 댓글 조회 API",
            description = "게시글에 달린 댓글을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "댓글 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "게시글 ID가 입력되지 않았습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없습니다.")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentResponseDTO>>> getCommentsByArticleId(
            @RequestParam Long articleId
    ) {

        // 게시글 ID 누락시 예외처리
        if (articleId == null) {
            throw new NotFoundException(ErrorStatus.MISSING_COMMENT_ARTICLEID.getMessage());
        }

        List<CommentResponseDTO> comments = commentService.getCommentsByArticleId(articleId);

        return ApiResponse.success(SuccessStatus.GET_COMMENT_SUCCESS, comments);
    }
}
