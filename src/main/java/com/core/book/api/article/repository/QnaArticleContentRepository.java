package com.core.book.api.article.repository;

import com.core.book.api.article.entity.QnaArticle;
import com.core.book.api.article.entity.QnaArticleContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QnaArticleContentRepository extends JpaRepository<QnaArticleContent, Long> {
    List<QnaArticleContent> findByQnaArticle(QnaArticle qnaArticle);

}