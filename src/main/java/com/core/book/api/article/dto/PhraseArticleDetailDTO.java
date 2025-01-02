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
    private final String date;
    private final boolean myLike;

    private final long likeCnt;
    private final long quoCnt;
    private final long commentCnt;

    private final List<PhraseArticleContentDetailDTO> phraseContents;

    @Builder
    public PhraseArticleDetailDTO(Long articleId,
                                  Long memberId,
                                  String nickname,
                                  String profileImage,
                                  String date,
                                  long likeCnt,
                                  long quoCnt,
                                  long commentCnt,
                                  long followerCount,
                                  
                                  List<PhraseArticleContentDetailDTO> phraseContents,
                                  boolean myLike) {
      
        this.articleId = articleId;
        this.memberId = memberId;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.likeCnt = likeCnt;
        this.quoCnt = quoCnt;
        this.commentCnt = commentCnt;
        this.phraseContents = phraseContents;
        this.followerCount = followerCount;
        this.date = date;
        this.myLike = myLike;

    }
}
