package com.core.book.api.article.repository;

import com.core.book.api.article.entity.ArticleType;
import com.core.book.api.article.entity.ReviewArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewArticleRepository extends JpaRepository<ReviewArticle, Long> {

    @Query(value = "SELECT ra FROM ReviewArticle ra JOIN FETCH ra.member m JOIN FETCH ra.book b",
            countQuery = "SELECT COUNT(ra) FROM ReviewArticle ra")
    Page<ReviewArticle> findAllWithFetchJoin(Pageable pageable);

    @EntityGraph(attributePaths = {"member", "book"})
    Page<ReviewArticle> findByTypeIn(List<ArticleType> types, Pageable pageable);

    @EntityGraph(attributePaths = {"member", "book"})
    Page<ReviewArticle> findByType(ArticleType type, Pageable pageable);

    @EntityGraph(attributePaths = {"member", "book"})
    Page<ReviewArticle> findAll(Pageable pageable);
}