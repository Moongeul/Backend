package com.core.book.api.comment.repository;

import com.core.book.api.article.entity.Article;
import com.core.book.api.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByArticle(Article article);
}
