package com.core.book.api.article.repository;

import com.core.book.api.article.entity.PhraseArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhraseArticleRepository extends JpaRepository<PhraseArticle, Long> {

}