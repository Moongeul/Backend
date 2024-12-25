package com.core.book.api.article.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PhraseArticleDetailDTO {

    private final Long articleId;
    private final Long memberId;
    private final String nickname;
    private final String profileImage;
    private final long followerCount;

    private final long likeCnt;
    private final long quoCnt;
    private final long commentCnt;

    private final List<PhraseArticleContentDetailDTO> phraseContents;

    @Builder
    public PhraseArticleDetailDTO(Long articleId,
                                  Long memberId,
                                  String nickname,
                                  String profileImage,
                                  long likeCnt,
                                  long quoCnt,
                                  long commentCnt,
                                  long followerCount,
                                  List<PhraseArticleContentDetailDTO> phraseContents) {
        this.articleId = articleId;
        this.memberId = memberId;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.likeCnt = likeCnt;
        this.quoCnt = quoCnt;
        this.commentCnt = commentCnt;
        this.phraseContents = phraseContents;
        this.followerCount = followerCount;
    }
}
