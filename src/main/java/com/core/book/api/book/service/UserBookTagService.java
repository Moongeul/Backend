package com.core.book.api.book.service;

import com.core.book.api.article.entity.ReviewArticle;
import com.core.book.api.book.constant.BookTag;
import com.core.book.api.book.dto.UserBookTagDTO;
import com.core.book.api.book.entity.Book;
import com.core.book.api.book.entity.UserBookTag;
import com.core.book.api.book.repository.UserBookTagRepository;
import com.core.book.api.bookshelf.entity.ReadBooks;
import com.core.book.common.exception.NotFoundException;
import com.core.book.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserBookTagService {
    private final UserBookTagRepository userBookTagRepository;

    // 태그 수정 메서드
    public void updateUserBookTag(List<UserBookTagDTO> tagList, Book book, ReadBooks readBooks, ReviewArticle reviewArticle){
        if (tagList != null) {
            for (UserBookTagDTO tagDTO : tagList) {
                if (tagDTO == null) continue;  // null 태그가 있을 경우 건너뛰기
                BookTag tagEnum = BookTag.fromDescription(tagDTO.getTag());

                if (tagDTO.getTagId() == 0) { // 태그 새로 저장

                    UserBookTag newUserBookTag = tagDTO.toEntity(book, tagEnum, readBooks, reviewArticle);
                    userBookTagRepository.save(newUserBookTag);

                } else { // 태그 수정
                    UserBookTag existTag = userBookTagRepository.findById(tagDTO.getTagId()).
                            orElseThrow(() -> new NotFoundException(ErrorStatus.TAG_NOT_FOUND_EXCPETION.getMessage()));

                    // 1) 태그 이름 변경 O -> 데이터 변경 (변경 X -> 그대로 유지)
                    if (tagEnum.getId() != existTag.getTag()) {

                        UserBookTag updatedTag = tagDTO.update(existTag, tagEnum);
                        userBookTagRepository.save(updatedTag);
                    }
                }
            }
        }
    }

}
