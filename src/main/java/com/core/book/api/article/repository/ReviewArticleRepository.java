package com.core.book.api.article.repository;

import com.core.book.api.article.entity.ReviewArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewArticleRepository extends JpaRepository<ReviewArticle, Long> {

    @Query(value = "SELECT ra FROM ReviewArticle ra JOIN FETCH ra.member m JOIN FETCH ra.book b",
            countQuery = "SELECT COUNT(ra) FROM ReviewArticle ra")
    Page<ReviewArticle> findAllWithFetchJoin(Pageable pageable);
}