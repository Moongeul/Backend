package com.core.book.api.article.service;

import com.core.book.api.article.dto.*;
import com.core.book.api.article.entity.*;
import com.core.book.api.article.repository.ArticleLikeRepository;
import com.core.book.api.article.repository.ArticleRepository;
import com.core.book.api.article.repository.PhraseArticleRepository;
import com.core.book.api.article.repository.ReviewArticleRepository;
import com.core.book.api.book.entity.Book;
import com.core.book.api.member.entity.Member;
import com.core.book.api.member.repository.FollowRepository;
import com.core.book.api.member.service.MemberService;
import com.core.book.common.exception.NotFoundException;
import com.core.book.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleViewService {

    private final ReviewArticleRepository reviewArticleRepository;
    private final PhraseArticleRepository phraseArticleRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleRepository articleRepository;
    private final FollowRepository followRepository;
    private final MemberService memberService;

    public ArticleListResponseDTO getAllArticles(String articleType, int page, int size, UserDetails userDetails) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if ("all".equalsIgnoreCase(articleType)) {
            // all: REVIEW, PHRASE, QNA 조회
            List<ArticleType> targetTypes = Arrays.asList(ArticleType.REVIEW, ArticleType.PHRASE, ArticleType.QNA);
            Page<Article> articlePage = articleRepository.findByTypeIn(targetTypes, pageable);

            List<ArticleListDTO> articles = articlePage.getContent().stream()
                    .map(article -> convertToListDTO(article, userDetails))
                    .collect(Collectors.toList());

            return new ArticleListResponseDTO(articles, articlePage.isLast(), page);
        } else {
            // 특정 게시글 타입
            return getArticlesByType(articleType, page, size, userDetails);
        }
    }

    private ArticleListResponseDTO getArticlesByType(String articleType, int page, int size, UserDetails userDetails) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        ArticleType type;

        try {
            type = ArticleType.valueOf(articleType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(ErrorStatus.ARTICLE_TYPE_NOT_FOUND_EXCEPTION.getMessage());
        }

        Page<Article> articlePage = articleRepository.findByType(type, pageable);

        List<ArticleListDTO> articles = articlePage.getContent().stream()
                .map(article -> convertToListDTO(article, userDetails))
                .collect(Collectors.toList());

        return new ArticleListResponseDTO(articles, articlePage.isLast(), page);
    }

    private ArticleListDTO convertToListDTO(Article article, UserDetails userDetails) {
        Member member = article.getMember(); // 작성자 정보
        Book book = article.getBook();       // 책 정보

        boolean myLike = false;
        if (userDetails != null) {
            Long userId = memberService.getUserIdByEmail(userDetails.getUsername());
            myLike = articleLikeRepository.findByArticleIdAndMemberId(article.getId(), userId).isPresent();
        }

        return ArticleListDTO.builder()
                .articleId(article.getId())
                .memberId(member.getId())
                .profileImage(member.getImageUrl())
                .nickname(member.getNickname())
                .content(article.getContent())
                .likeCnt(article.getLikeCnt())
                .commentCnt(article.getCommentCnt())
                .quoCnt(article.getQuoCnt())
                .bookImage(book.getBookImage())
                .title(book.getTitle())
                .author(book.getAuthor())
                .articleType(article.getType())
                .myLike(myLike)
                .build();
    }

    // 감상평 게시글 상세 조회
    public ReviewArticleDetailDTO getReviewArticleDetail(Long id, UserDetails userDetails) {
        ReviewArticle reviewArticle = reviewArticleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.ARTICLE_NOT_FOUND_EXCEPTION.getMessage()));

        Book book = reviewArticle.getBook();
        Member member = reviewArticle.getMember();
        long followerCount = followRepository.countByFollowingId(member.getId());

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

        boolean myLike = false;
        if (userDetails != null) {
            Long userId = memberService.getUserIdByEmail(userDetails.getUsername());
            myLike = articleLikeRepository.findByArticleIdAndMemberId(reviewArticle.getId(), userId).isPresent();
        }

        return ReviewArticleDetailDTO.builder()
                .memberId(member.getId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .author(book.getAuthor())
                .bookImage(book.getBookImage())
                .content(reviewArticle.getContent())
                .oneLineReview(reviewArticle.getOneLineReview())
                .likeCnt(reviewArticle.getLikeCnt())
                .quoCnt(reviewArticle.getQuoCnt())
                .commentCnt(reviewArticle.getCommentCnt())
                .rating(reviewArticle.getRating())
                .reviewArticleTagDTO(reviewArticleTagDTO)
                .nickname(member.getNickname())
                .profileImage(member.getImageUrl())
                .followerCount(followerCount)
                .myLike(myLike)
                .build();
    }

    // 인상깊은구절 게시글 상세 조회
    public PhraseArticleDetailDTO getPhraseArticleDetail(Long id, UserDetails userDetails) {
        PhraseArticle phraseArticle = phraseArticleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.ARTICLE_NOT_FOUND_EXCEPTION.getMessage()));

        Book book = phraseArticle.getBook();
        Member member = phraseArticle.getMember();
        long followerCount = followRepository.countByFollowingId(member.getId());

        boolean myLike = false;
        if (userDetails != null) {
            Long userId = memberService.getUserIdByEmail(userDetails.getUsername());
            myLike = articleLikeRepository.findByArticleIdAndMemberId(phraseArticle.getId(), userId).isPresent();
        }

        return PhraseArticleDetailDTO.builder()
                .memberId(member.getId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .author(book.getAuthor())
                .bookImage(book.getBookImage())
                .content(phraseArticle.getContent())
                .likeCnt(phraseArticle.getLikeCnt())
                .quoCnt(phraseArticle.getQuoCnt())
                .commentCnt(phraseArticle.getCommentCnt())
                .pageNum(phraseArticle.getPageNum())
                .phraseContent(phraseArticle.getPhraseContent())
                .nickname(member.getNickname())
                .profileImage(member.getImageUrl())
                .followerCount(followerCount)
                .myLike(myLike)
                .build();
    }
}
