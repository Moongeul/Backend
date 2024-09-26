package com.core.book.api.article.service;

import com.core.book.api.article.dto.ReviewArticleDetailDTO;
import com.core.book.api.article.dto.ReviewArticleListDTO;
import com.core.book.api.article.dto.ReviewArticleListResponseDTO;
import com.core.book.api.article.dto.ReviewArticleTagDTO;
import com.core.book.api.article.entity.ReviewArticle;
import com.core.book.api.article.entity.ReviewArticleTag;
import com.core.book.api.article.repository.ReviewArticleRepository;
import com.core.book.api.book.entity.Book;
import com.core.book.api.member.entity.Member;
import com.core.book.api.member.repository.FollowRepository;
import com.core.book.common.exception.NotFoundException;
import com.core.book.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleViewService {

    private final ReviewArticleRepository reviewArticleRepository;
    private final FollowRepository followRepository;

    public ReviewArticleDetailDTO getReviewArticleDetail(Long id) {
        // 게시글 조회
        ReviewArticle reviewArticle = reviewArticleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.ARTICLE_NOT_FOUND_EXCEPTION.getMessage()));

        // 책 정보 가져오기
        Book book = reviewArticle.getBook();

        // 작성자 정보 가져오기
        Member member = reviewArticle.getMember();

        // 작성자의 팔로워 수 조회
        long followerCount = followRepository.countByFollowingId(member.getId());

        // 태그 정보 가져오기
        ReviewArticleTag reviewArticleTag = reviewArticle.getReviewArticleTag();
        ReviewArticleTagDTO reviewArticleTagDTO = null;
        if (reviewArticleTag != null) {
            reviewArticleTagDTO = ReviewArticleTagDTO.builder()
                    .tag1(reviewArticleTag.getTag1())
                    .tag2(reviewArticleTag.getTag2())
                    .tag3(reviewArticleTag.getTag3())
                    .tag4(reviewArticleTag.getTag4())
                    .tag5(reviewArticleTag.getTag5())
                    .build();
        }

        return ReviewArticleDetailDTO.builder()
                .memberId(member.getId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .author(book.getAuthor())
                .bookImage(book.getBook_image())
                .content(reviewArticle.getContent())
                .oneLineReview(reviewArticle.getOneLineReview())
                .likeCnt(reviewArticle.getLikeCnt())
                .quoCnt(reviewArticle.getQuoCnt())
                .commentCnt(reviewArticle.getCommentCnt())
                .starRating(reviewArticle.getStarRating())
                .reviewArticleTagDTO(reviewArticleTagDTO)
                .nickname(member.getNickname())
                .profileImage(member.getImageUrl())
                .followerCount(followerCount)
                .build();
    }

    public ReviewArticleListResponseDTO getAllReviewArticles(int page, int size) {
        // 생성 날짜를 기준으로 최신 게시글 페이지네이션
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ReviewArticle> reviewArticlePage = reviewArticleRepository.findAllWithFetchJoin(pageable);

        List<ReviewArticleListDTO> articles = reviewArticlePage.getContent().stream()
                .map(this::convertToListDTO)
                .collect(Collectors.toList());
        boolean hasNext = reviewArticlePage.hasNext();

        return new ReviewArticleListResponseDTO(articles, hasNext);
    }

    public ReviewArticleListDTO convertToListDTO(ReviewArticle reviewArticle) {
        Member member = reviewArticle.getMember();
        Book book = reviewArticle.getBook();

        return ReviewArticleListDTO.builder()
                .articleId(reviewArticle.getId())
                .memberId(member.getId())
                .profileImage(member.getImageUrl())
                .nickname(member.getNickname())
                .content(reviewArticle.getContent())
                .likeCnt(reviewArticle.getLikeCnt())
                .commentCnt(reviewArticle.getCommentCnt())
                .quoCnt(reviewArticle.getQuoCnt())
                .bookImage(book.getBook_image())
                .title(book.getTitle())
                .author(book.getAuthor())
                .build();
    }
}