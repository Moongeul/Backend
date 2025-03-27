package com.core.book.api.comment.service;

import com.core.book.api.article.entity.Article;
import com.core.book.api.article.entity.QnaArticle;
import com.core.book.api.article.entity.QnaArticleContent;
import com.core.book.api.article.repository.ArticleRepository;
import com.core.book.api.article.repository.QnaArticleContentRepository;
import com.core.book.api.comment.dto.*;
import com.core.book.api.comment.entity.Comment;
import com.core.book.api.comment.entity.QnaComment;
import com.core.book.api.comment.repository.CommentRepository;
import com.core.book.api.comment.repository.QnaCommentRepository;
import com.core.book.api.member.entity.Member;
import com.core.book.api.member.repository.MemberRepository;
import com.core.book.common.exception.NotFoundException;
import com.core.book.common.exception.UnauthorizedException;
import com.core.book.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final QnaArticleContentRepository qnaArticleContentRepository;
    private final QnaCommentRepository qnaCommentRepository;

    // 댓글 생성
    @Transactional
    public void createComment(CommentCreateDTO commentCreateDTO, Long userId) {
        // 해당 유저를 찾을 수 없을 경우 예외처리
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));
        // 해당 게시글을 찾을 수 없을 경우 예외처리
        Article article = articleRepository.findById(commentCreateDTO.getArticleId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.ARTICLE_NOT_FOUND_EXCEPTION.getMessage()));
        // 부모댓글 처리
        Comment parentComment = null;
        if (commentCreateDTO.getParentId() != null) {
            parentComment = commentRepository.findById(commentCreateDTO.getParentId())
                    .orElseThrow(() -> new NotFoundException(ErrorStatus.PARENT_COMMENT_NOT_FOUND_EXCEPTION.getMessage()));
        }

        Comment comment = Comment.builder()
                .comment(commentCreateDTO.getComment())
                .article(article)
                .member(member)
                .parentComment(parentComment)
                .build();

        commentRepository.save(comment);

        // 댓글 수 증가
        article.increaseCommentCount();
    }

    //댓글 조회
    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getCommentsByArticleId(Long articleId) {
        // 해당 게시글을 찾을 수 없을 경우 예외처리
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.ARTICLE_NOT_FOUND_EXCEPTION.getMessage()));

        List<Comment> comments = commentRepository.findByArticle(article);

        return comments.stream()
                .map(CommentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    //댓글 수정
    @Transactional
    public void updateComment(Long commentId, CommentUpdateDTO commentUpdateDTO, Long userId) {
        // 댓글이 존재하지 않으면 예외 처리
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.COMMENT_NOT_FOUND_EXCPETION.getMessage()));

        // 해당 댓글의 작성자가 요청한 사용자와 동일한지 검증
        if (!comment.getMember().getId().equals(userId)) {
            throw new UnauthorizedException(ErrorStatus.INVALID_MODIFY_AUTH.getMessage());
        }

        comment = comment.toBuilder()
                .comment(commentUpdateDTO.getComment())
                .build();

        commentRepository.save(comment);
    }

    //댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        // 댓글을 ID로 찾고, 존재하지 않으면 예외 처리
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.COMMENT_NOT_FOUND_EXCPETION.getMessage()));

        // 해당 댓글의 작성자가 요청한 사용자와 동일한지 검증
        if (!comment.getMember().getId().equals(userId)) {
            throw new UnauthorizedException(ErrorStatus.INVALID_DELETE_AUTH.getMessage());
        }

        commentRepository.delete(comment);

        // 댓글 수 감소
        Article article = comment.getArticle();
        article.decreaseCommentCount();
    }

    // QnA 답변(댓글) 생성
    @Transactional
    public void createQnaComment(QnaCommentCreateDTO qnaCommentCreateDTO, Long userId) {
        // 해당 유저를 찾을 수 없을 경우 예외처리
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        // QnA 게시글의 질문(문단) 확인
        QnaArticleContent qnaArticleContent = qnaArticleContentRepository.findById(qnaCommentCreateDTO.getQnaCommentId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.ARTICLE_NOT_FOUND_EXCEPTION.getMessage()));

        // 실제 QnAArticle 꺼내기 (댓글 수 증가용)
        QnaArticle qnaArticle = qnaArticleContent.getQnaArticle();

        // 부모 댓글 확인
        QnaComment parentComment = null;
        if (qnaCommentCreateDTO.getParentId() != null) {
            parentComment = qnaCommentRepository.findById(qnaCommentCreateDTO.getParentId())
                    .orElseThrow(() -> new NotFoundException(ErrorStatus.PARENT_COMMENT_NOT_FOUND_EXCEPTION.getMessage()));
        }

        QnaComment qnaComment = QnaComment.builder()
                .comment(qnaCommentCreateDTO.getComment())
                .qnaArticleContent(qnaArticleContent)
                .member(member)
                .parentComment(parentComment)
                .build();
        qnaCommentRepository.save(qnaComment);

        // 댓글 수 증가 로직
        qnaArticle.increaseCommentCount();
    }

    // QnA 답변(댓글) 조회
    @Transactional(readOnly = true)
    public List<QnaCommentResponseDTO> getQnACommentsByQnaCommentId(Long QnaCommentId) {
        // 해당 질문을 찾을 수 없을 경우 예외처리
        QnaArticleContent qnaArticleContent = qnaArticleContentRepository.findById(QnaCommentId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.ARTICLE_NOT_FOUND_EXCEPTION.getMessage()));

        List<QnaComment> comments = qnaCommentRepository.findByQnaArticleContent(qnaArticleContent);

        return comments.stream()
                .map(QnaCommentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // QnA 답변(댓글) 수정
    @Transactional
    public void updateQnaComment(Long commentId, CommentUpdateDTO commentUpdateDTO, Long userId) {
        // 답변(댓글)이 존재하지 않으면 예외 처리
        QnaComment qnaComment = qnaCommentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.COMMENT_NOT_FOUND_EXCPETION.getMessage()));

        // 해당 댓글의 작성자가 요청한 사용자와 동일한지 검증
        if (!qnaComment.getMember().getId().equals(userId)) {
            throw new UnauthorizedException(ErrorStatus.INVALID_MODIFY_AUTH.getMessage());
        }

        qnaComment = qnaComment.toBuilder()
                .comment(commentUpdateDTO.getComment())
                .build();

        qnaCommentRepository.save(qnaComment);
    }

    // QnA 답변(댓글) 삭제
    @Transactional
    public void deleteQnaComment(Long commentId, Long userId) {
        // 답변(댓글)이 존재하지 않으면 예외 처리
        QnaComment qnaComment = qnaCommentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.COMMENT_NOT_FOUND_EXCPETION.getMessage()));

        // 해당 댓글의 작성자가 요청한 사용자와 동일한지 검증
        if (!qnaComment.getMember().getId().equals(userId)) {
            throw new UnauthorizedException(ErrorStatus.INVALID_DELETE_AUTH.getMessage());
        }

        qnaCommentRepository.delete(qnaComment);

        // 댓글 수 감소
        QnaArticle qnaArticle = qnaComment.getQnaArticleContent().getQnaArticle();
        qnaArticle.decreaseCommentCount();

    }
}
