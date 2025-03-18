package com.core.book.api.article.repository;

import com.core.book.api.article.entity.Article;
import com.core.book.api.article.entity.ArticleLike;
import com.core.book.api.article.entity.ArticleType;
import com.core.book.api.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
    Optional<ArticleLike> findByArticleIdAndMemberId(Long articleId, Long userId);
    List<ArticleLike> findByArticle(Article article);

    @Query("SELECT al FROM ArticleLike al " +
            "JOIN FETCH al.article a " +
            "LEFT JOIN FETCH a.book " +
            "WHERE al.member = :member " +
            "AND a.type IN :types")
    Page<ArticleLike> findByMemberAndArticleTypeIn(@Param("member") Member member,
                                                   @Param("types") List<ArticleType> types,
                                                   Pageable pageable);

}