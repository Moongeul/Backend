package com.core.book.api.article.repository;

import com.core.book.api.article.entity.Article;
import com.core.book.api.article.entity.ArticleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // 특정 사용자 ID를 통해 작성한 게시글(Phrase, QNA, Review) 가져오기
    @Query("SELECT a FROM Article a " +
            "WHERE a.type IN :types " +
            "AND ( (TYPE(a) = ReviewArticle AND TREAT(a AS ReviewArticle).member.id = :userId) " +
            "OR (TYPE(a) = PhraseArticle AND TREAT(a AS PhraseArticle).member.id = :userId) " +
            "OR (TYPE(a) = QnaArticle AND TREAT(a AS QnaArticle).member.id = :userId) )")
    Page<Article> findByUserIdAndTypes(@Param("userId") Long userId,
                                       @Param("types") List<ArticleType> types,
                                       Pageable pageable);

}
