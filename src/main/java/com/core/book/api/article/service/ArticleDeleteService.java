package com.core.book.api.article.service;

import com.core.book.api.article.entity.*;
import com.core.book.api.article.repository.*;
import com.core.book.api.book.entity.UserBookTag;
import com.core.book.api.book.repository.UserBookTagRepository;
import com.core.book.api.comment.entity.Comment;
import com.core.book.api.comment.entity.QnaComment;
import com.core.book.api.comment.repository.CommentRepository;
import com.core.book.api.comment.repository.QnaCommentRepository;
import com.core.book.common.exception.NotFoundException;
import com.core.book.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleDeleteService {

    private final CommentRepository commentRepository;
    private final QnaArticleContentRepository qnaArticleContentRepository;
    private final QnaCommentRepository qnaCommentRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final ReviewArticleRepository reviewArticleRepository;
    private final PhraseArticleRepository phraseArticleRepository;
    private final QnaArticleRepository qnaArticleRepository;
    private final QuotationArticleRepository quotationArticleRepository;
    private final UserBookTagRepository userBookTagRepository;

    // 게시글에 연관된 좋아요 삭제
    @Transactional
    protected void deleteArticleLikesForArticle(Article article) {
        List<ArticleLike> articleLikes = articleLikeRepository.findByArticle(article);
        if (articleLikes != null && !articleLikes.isEmpty()) {
            articleLikeRepository.deleteAll(articleLikes);
        }
    }

    // 삭제할 원 게시글을 인용한 인용 게시글들을 삭제
    @Transactional
    protected void deleteQuotationArticlesForArticle(Article article) {
        List<QuotationArticle> quotationArticles = article.getQuotationArticles();
        if (quotationArticles != null && !quotationArticles.isEmpty()) {
            for (QuotationArticle qa : quotationArticles) {
                // 하위 인용 게시글 재귀 삭제
                deleteQuotationArticlesForArticle(qa);

                // 인용 게시글에 달린 댓글 전부 삭제
                deleteCommentsForArticle(qa);

                // 인용 게시글에 달린 좋아요 전부 삭제
                deleteArticleLikesForArticle(qa);
            }
            quotationArticleRepository.deleteAll(quotationArticles);
        }
    }

    // 일반 댓글 삭제
    @Transactional
    protected void deleteCommentsForArticle(Article article) {
        commentRepository.deleteAllByArticleIdNative(article.getId());
    }

    // QnA 댓글 삭제
    @Transactional
    protected void deleteQnaCommentsForQnaArticle(QnaArticle qnaArticle) {
        List<QnaArticleContent> contents = qnaArticleContentRepository.findByQnaArticle(qnaArticle);
        if (contents != null && !contents.isEmpty()) {
            for (QnaArticleContent content : contents) {
                List<QnaComment> qnaComments = qnaCommentRepository.findByQnaArticleContent(content);
                if (qnaComments != null && !qnaComments.isEmpty()) {
                    qnaCommentRepository.deleteAll(qnaComments);
                }
            }
        }
    }

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

        // 연관된 좋아요, 댓글, 인용 삭제
        deleteQuotationArticlesForArticle(reviewArticle);
        deleteCommentsForArticle(reviewArticle);
        deleteArticleLikesForArticle(reviewArticle);

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

        // 연관된 좋아요, 댓글, 인용 삭제
        deleteQuotationArticlesForArticle(phraseArticle);
        deleteCommentsForArticle(phraseArticle);
        deleteArticleLikesForArticle(phraseArticle);

        phraseArticleRepository.delete(phraseArticle);
    }

    // QnA 게시글 삭제
    @Transactional
    public void deleteQnaArticle(Long articleId, Long userId) {
        QnaArticle qnaArticle = qnaArticleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.ARTICLE_NOT_FOUND_EXCEPTION.getMessage()));

        // 게시글 작성자와 삭제 요청자가 다를 경우 예외 처리
        if (!qnaArticle.getMember().getId().equals(userId)) {
            throw new NotFoundException(ErrorStatus.ARTICLE_DELETE_NOT_SAME_USER_EXCEPTION.getMessage());
        }

        // 연관된 좋아요, 댓글, 인용 삭제
        deleteQuotationArticlesForArticle(qnaArticle);
        deleteCommentsForArticle(qnaArticle);
        deleteQnaCommentsForQnaArticle(qnaArticle);
        deleteArticleLikesForArticle(qnaArticle);

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

        // 연관된 좋아요, 댓글, 인용 삭제
        deleteQuotationArticlesForArticle(quotationArticle);
        deleteCommentsForArticle(quotationArticle);
        deleteArticleLikesForArticle(quotationArticle);

        // 원 게시글 인용 수 감소
        Article quotedArticle = quotationArticle.getQuotedArticle();
        quotedArticle.decreaseQuoCount();

        quotationArticleRepository.delete(quotationArticle);
    }

}
