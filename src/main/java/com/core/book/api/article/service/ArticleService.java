package com.core.book.api.article.service;

import com.core.book.api.article.entity.Article;
import com.core.book.api.article.entity.ArticleLike;
import com.core.book.api.article.repository.ArticleLikeRepository;
import com.core.book.api.article.repository.ArticleRepository;
import com.core.book.api.member.entity.Member;
import com.core.book.api.member.repository.MemberRepository;
import com.core.book.common.exception.NotFoundException;
import com.core.book.common.response.ErrorStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;

    // 좋아요 토글
    public void toggleLike(Long articleId, Long userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.ARTICLE_NOT_FOUND_EXCEPTION.getMessage()));

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        Optional<ArticleLike> existingLike = articleLikeRepository.findByArticleIdAndMemberId(articleId, userId);

        if (existingLike.isPresent()) {
            articleLikeRepository.delete(existingLike.get());
            article = article.decreaseLikeCount();
        } else {
            ArticleLike articleLike = ArticleLike.builder()
                    .article(article)
                    .member(member)
                    .build();
            articleLikeRepository.save(articleLike);
            article = article.increaseLikeCount();
        }

        articleRepository.save(article);
    }
}