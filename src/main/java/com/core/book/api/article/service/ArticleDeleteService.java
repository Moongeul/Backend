package com.core.book.api.article.service;

import com.core.book.api.article.entity.PhraseArticle;
import com.core.book.api.article.entity.QuotationArticle;
import com.core.book.api.article.entity.ReviewArticle;
import com.core.book.api.article.repository.PhraseArticleRepository;
import com.core.book.api.article.entity.QnaArticle;
import com.core.book.api.article.repository.QnaArticleRepository;
import com.core.book.api.article.repository.ReviewArticleRepository;
import com.core.book.api.article.repository.QuotationArticleRepository;
import com.core.book.api.book.entity.UserBookTag;
import com.core.book.api.book.repository.UserBookTagRepository;
import com.core.book.common.exception.NotFoundException;
import com.core.book.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleDeleteService {

    private final ReviewArticleRepository reviewArticleRepository;
    private final PhraseArticleRepository phraseArticleRepository;
    private final QnaArticleRepository qnaArticleRepository;
    private final QuotationArticleRepository quotationArticleRepository;
    private final UserBookTagRepository userBookTagRepository;

    //감상평 게시글 삭제
    @Transactional
    public void deleteReviewArticle(Long articleId, Long userId) {
        ReviewArticle reviewArticle = reviewArticleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.ARTICLE_NOT_FOUND_EXCEPTION.getMessage()));

        // 게시글 작성자와 삭제 요청자가 다를 경우 예외 처리
        if (!reviewArticle.getMember().getId().equals(userId)) {
            throw new NotFoundException(ErrorStatus.ARTICLE_DELETE_NOT_SAME_USER_EXCEPTION.getMessage());
        }

        /* UserBookTag 삭제 */
        List<UserBookTag> userBookTags = userBookTagRepository.findByReviewArticle(reviewArticle);

        // 조회된 UserBookTag가 있으면 삭제
        if(!userBookTags.isEmpty()){
            // 튜플 삭제
            userBookTagRepository.deleteAll(userBookTags);
        }

        reviewArticleRepository.delete(reviewArticle);
    }

    // 인상깊은구절 게시글 삭제
    @Transactional
    public void deletePhraseArticle(Long articleId, Long userId) {
        PhraseArticle phraseArticle = phraseArticleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.ARTICLE_NOT_FOUND_EXCEPTION.getMessage()));

        // 게시글 작성자와 삭제 요청자가 다를 경우 예외 처리
        if (!phraseArticle.getMember().getId().equals(userId)) {
            throw new NotFoundException(ErrorStatus.ARTICLE_DELETE_NOT_SAME_USER_EXCEPTION.getMessage());
        }

        phraseArticleRepository.delete(phraseArticle);
    }

    // 인상깊은구절 게시글 삭제
    @Transactional
    public void deleteQnaArticle(Long articleId, Long userId) {
        QnaArticle qnaArticle = qnaArticleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.ARTICLE_NOT_FOUND_EXCEPTION.getMessage()));

        // 게시글 작성자와 삭제 요청자가 다를 경우 예외 처리
        if (!qnaArticle.getMember().getId().equals(userId)) {
            throw new NotFoundException(ErrorStatus.ARTICLE_DELETE_NOT_SAME_USER_EXCEPTION.getMessage());
        }

        qnaArticleRepository.delete(qnaArticle);
    }

    // 인용 게시글 삭제
    @Transactional
    public void deleteQuotationArticle(Long articleId, Long userId) {
        QuotationArticle quotationArticle = quotationArticleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.ARTICLE_NOT_FOUND_EXCEPTION.getMessage()));

        // 게시글 작성자와 삭제 요청자가 다를 경우 예외 처리
        if (!quotationArticle.getMember().getId().equals(userId)) {
            throw new NotFoundException(ErrorStatus.ARTICLE_DELETE_NOT_SAME_USER_EXCEPTION.getMessage());
        }

        quotationArticleRepository.delete(quotationArticle);
    }

}
