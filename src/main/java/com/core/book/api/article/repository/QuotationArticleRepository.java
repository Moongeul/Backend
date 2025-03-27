package com.core.book.api.article.repository;

import com.core.book.api.article.entity.QuotationArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuotationArticleRepository extends JpaRepository<QuotationArticle, Long> {
    Page<QuotationArticle> findByQuotedArticleId(Long quotedArticleId, Pageable pageable);

}