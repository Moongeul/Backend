package com.core.book.api.comment.service;

import com.core.book.api.article.entity.Article;
import com.core.book.api.article.repository.ArticleRepository;
import com.core.book.api.comment.dto.CommentCreateDTO;
import com.core.book.api.comment.dto.CommentResponseDTO;
import com.core.book.api.comment.entity.Comment;
import com.core.book.api.comment.repository.CommentRepository;
import com.core.book.api.member.entity.Member;
import com.core.book.api.member.repository.MemberRepository;
import com.core.book.common.exception.NotFoundException;
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
    }

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
}
