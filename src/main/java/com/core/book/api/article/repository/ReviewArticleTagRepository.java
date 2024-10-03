package com.core.book.api.article.repository;

import com.core.book.api.article.entity.ReviewArticleTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewArticleTagRepository extends JpaRepository<ReviewArticleTag, Long> {
}
