package com.core.book.api.comment.repository;

import com.core.book.api.article.entity.Article;
import com.core.book.api.article.entity.QnaArticleContent;
import com.core.book.api.comment.entity.QnaComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QnaCommentRepository extends JpaRepository<QnaComment, Long> {
    List<QnaComment> findByQnaArticleContent(QnaArticleContent qnaArticleContent);

    @Query("SELECT DISTINCT qc.qnaArticleContent.qnaArticle FROM QnaComment qc " +
            "WHERE qc.member.id = :userId")
    List<Article> findDistinctQnaArticleByMemberId(@Param("userId") Long userId);
}
