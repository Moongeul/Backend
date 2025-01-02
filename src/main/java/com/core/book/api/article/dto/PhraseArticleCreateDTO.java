package com.core.book.api.article.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PhraseArticleCreateDTO {

    private List<PhraseArticleContentDTO> phraseContents;

    @Builder
    public PhraseArticleCreateDTO(List<PhraseArticleContentDTO> phraseContents) {
        this.phraseContents = phraseContents;
    }
}
