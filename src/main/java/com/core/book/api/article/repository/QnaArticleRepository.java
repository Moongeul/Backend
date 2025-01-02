package com.core.book.api.article.repository;

import com.core.book.api.article.entity.QnaArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QnaArticleRepository extends JpaRepository<QnaArticle, Long> {

}