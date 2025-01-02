package com.core.book.api.comment.repository;

import com.core.book.api.article.entity.QnaArticleContent;
import com.core.book.api.comment.entity.QnaComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QnaCommentRepository extends JpaRepository<QnaComment, Long> {
    List<QnaComment> findByQnaArticleContent(QnaArticleContent qnaArticleContent);
}
