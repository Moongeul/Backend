package com.core.book.api.article.service;

import com.core.book.api.article.entity.ReviewArticle;
import com.core.book.api.article.repository.ReviewArticleRepository;
import com.core.book.common.exception.NotFoundException;
import com.core.book.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleDeleteService {

    private final ReviewArticleRepository reviewArticleRepository;

    public void deleteReviewArticle(Long articleId, Long userId) {
        ReviewArticle reviewArticle = reviewArticleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.ARTICLE_NOT_FOUND_EXCEPTION.getMessage()));

        // 게시글 작성자와 삭제 요청자가 다를 경우 예외 처리
        if (!reviewArticle.getMember().getId().equals(userId)) {
            throw new NotFoundException(ErrorStatus.ARTICLE_DELETE_NOT_SAME_USER_EXCEPTION.getMessage());
        }

        reviewArticleRepository.delete(reviewArticle);
    }
}
