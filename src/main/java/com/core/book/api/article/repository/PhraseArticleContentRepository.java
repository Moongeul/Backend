package com.core.book.api.article.repository;

import com.core.book.api.article.entity.PhraseArticleContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhraseArticleContentRepository extends JpaRepository<PhraseArticleContent, Long> {
}
