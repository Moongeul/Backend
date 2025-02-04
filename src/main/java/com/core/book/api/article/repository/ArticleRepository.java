package com.core.book.api.article.repository;

import com.core.book.api.article.entity.Article;
import com.core.book.api.article.entity.ArticleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findByType(ArticleType type, Pageable pageable);

    Page<Article> findByTypeIn(Iterable<ArticleType> types, Pageable pageable);

    // 특정 ISBN을 가진 최신 게시글 5개 가져오기
    List<Article> findByBookIsbnOrderByCreatedAtDesc(String isbn, Pageable pageable);
}
