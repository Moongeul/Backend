package com.core.book.api.article.repository;

import com.core.book.api.article.entity.QnaArticleContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QnaArticleContentRepository extends JpaRepository<QnaArticleContent, Long> {

}