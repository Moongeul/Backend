package com.core.book.api.comment.repository;

import com.core.book.api.article.entity.Article;
import com.core.book.api.article.entity.ArticleType;
import com.core.book.api.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByArticle(Article article);

    @Modifying
    @Query(value = "DELETE FROM comment WHERE article_id = :articleId", nativeQuery = true)
    void deleteAllByArticleIdNative(@Param("articleId") Long articleId);

    @Query("SELECT DISTINCT c.article FROM Comment c " +
            "WHERE c.member.id = :userId AND c.article.type IN :types")
    List<Article> findDistinctArticleByMemberIdAndArticleTypeIn(@Param("userId") Long userId,
                                                                @Param("types") List<ArticleType> types);
}
