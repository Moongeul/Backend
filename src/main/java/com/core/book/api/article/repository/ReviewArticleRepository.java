package com.core.book.api.article.repository;

import com.core.book.api.article.entity.ReviewArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewArticleRepository extends JpaRepository<ReviewArticle, Long> {
}
