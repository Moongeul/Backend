package com.core.book.api.article.controller;

import com.core.book.api.article.dto.PhraseArticleCreateDTO;
import com.core.book.api.article.dto.QnaArticleCreateDTO;
import com.core.book.api.article.dto.ReviewArticleCreateDTO;
import com.core.book.api.article.service.ArticleCreateService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Article", description = "게시글 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/article")
public class ArticleCreateController {

    private final ArticleCreateService articleCreateService;
    private final MemberService memberService;

    @Operation(
            summary = "감상평 게시글 생성 API",
            description = "감상평 게시글을 생성합니다. (TYPE : REVIEW)" +
                    "\n- 만약 태그 전체가 입력되지 않았을 경우 : \"userBookTagList\": null" +
                    "\n- 수정의 경우, 기존에 있던 id는 순서 그대로, 새로 추가된 태그는 id를 0으로 보내시면 됩니다. 만약 기존에 있던 태그가 일부 삭제된 경우, id는 그대로, \"tag\": null 로 보내주시면 됩니다." +
                    "\n- 변경된 태그 요청 방식에 대한 상세 설명 : https://www.notion.so/api-v1-bookshelf-read-id-1a95d7dcdd5f42ad82aa5bc18e5588a9?pvs=4 해당 노션 설명 참고해주세요"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "게시글 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "해당 도서를 읽은 기록이 없습니다.")
    })
    @PostMapping("/review")
    public ResponseEntity<ApiResponse<Void>> createReviewArticle(
            @RequestBody ReviewArticleCreateDTO reviewArticleCreateDTO,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // content 누락시 예외처리
        if (reviewArticleCreateDTO.getContent() == null || reviewArticleCreateDTO.getContent().isEmpty()) {
            throw new NotFoundException(ErrorStatus.VALIDATION_CONTENT_MISSING_EXCEPTION.getMessage());
        }

        Long userId = memberService.getUserIdByEmail(userDetails.getUsername());
        articleCreateService.createReviewArticle(reviewArticleCreateDTO, userId);

        return ApiResponse.success_only(SuccessStatus.CREATE_ARTICLE_SUCCESS);
    }

    @Operation(
            summary = "인상깊은구절 게시글 생성 API",
            description = "인상깊은구절 게시글을 생성합니다. (TYPE : PHRASE) / 여러개의 구절을 등록할때 {\n" +
                    "      \"isbn\": \"(데이터)\",\n" +
                    "      \"content\": \"(데이터)\",\n" +
                    "      \"pageNum\": (데이터),\n" +
                    "      \"phraseContent\": \"(데이터)\"\n" +
                    "    } 해당부분을 ,로 구분하여 추가하면 됩니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "게시글 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "해당 도서를 읽은 기록이 없습니다.")
    })
    @PostMapping("/phrase")
    public ResponseEntity<ApiResponse<Void>> createPhraseArticle(
            @RequestBody PhraseArticleCreateDTO phraseArticleCreateDTO,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        if (phraseArticleCreateDTO.getPhraseContents() == null
                || phraseArticleCreateDTO.getPhraseContents().isEmpty()) {
            throw new NotFoundException(ErrorStatus.VALIDATION_CONTENT_MISSING_EXCEPTION.getMessage());
        }

        Long userId = memberService.getUserIdByEmail(userDetails.getUsername());
        articleCreateService.createPhraseArticle(phraseArticleCreateDTO, userId);

        return ApiResponse.success_only(SuccessStatus.CREATE_ARTICLE_SUCCESS);
    }

    @Operation(
            summary = "QnA 게시글 생성 API",
            description = "QnA 게시글을 생성합니다. (TYPE : QNA) / 여러개의 질문을 등록할때 {\n" +
                    "      \"content\": \"(데이터)\"\n" +
                    "    } 해당부분을 ,로 구분하여 추가하면 됩니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "게시글 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "해당 도서를 읽은 기록이 없습니다.")
    })
    @PostMapping("/qna")
    public ResponseEntity<ApiResponse<Void>> createQnaArticle(
            @RequestBody QnaArticleCreateDTO qnaArticleCreateDTO,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        if (qnaArticleCreateDTO.getQnaContents() == null
                || qnaArticleCreateDTO.getQnaContents().isEmpty()) {
            throw new NotFoundException(ErrorStatus.VALIDATION_CONTENT_MISSING_EXCEPTION.getMessage());
        }

        Long userId = memberService.getUserIdByEmail(userDetails.getUsername());
        articleCreateService.createQnaArticle(qnaArticleCreateDTO, userId);

        return ApiResponse.success_only(SuccessStatus.CREATE_ARTICLE_SUCCESS);
    }

}
