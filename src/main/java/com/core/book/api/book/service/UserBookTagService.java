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

    // id로 태그 entity 가져오기
    public UserBookTag findUserBookTag(int tagId){
        return userBookTagRepository.findById(tagId).
                orElseThrow(() -> new NotFoundException(ErrorStatus.TAG_NOT_FOUND_EXCPETION.getMessage()));
    }

    // 태그 수정 메서드
    public void updateUserBookTag(List<UserBookTagDTO> tagList, Book book, ReadBooks readBooks, ReviewArticle reviewArticle){
        if (tagList != null) {
            for (UserBookTagDTO tagDTO : tagList) {
                if (tagDTO == null) continue;  // null 태그가 있을 경우 건너뛰기

                // 태그 삭제 -> 태그 이름이 null 인 경우
                if(tagDTO.getTag() == null){
                    UserBookTag existTag = findUserBookTag(tagDTO.getTagId());
                    userBookTagRepository.delete(existTag);
                    continue;
                }
                BookTag tagEnum = BookTag.fromDescription(tagDTO.getTag());

                // 태그 새로 저장 -> 태그 id가 0인 경우
                if (tagDTO.getTagId() == 0) {

                    UserBookTag newUserBookTag = tagDTO.toEntity(book, tagEnum, readBooks, reviewArticle);
                    userBookTagRepository.save(newUserBookTag);

                } else { // 태그 수정 -> 태그 id가 있는 경우
                    UserBookTag existTag = findUserBookTag(tagDTO.getTagId());

                    // 태그 이름이 변경되었다면 수정
                    if (tagEnum.getId() != existTag.getTag()) {

                        UserBookTag updatedTag = tagDTO.update(existTag, tagEnum);
                        userBookTagRepository.save(updatedTag);
                    }
                    
                    // 태그 이름이 그대로라면 변경 없음
                }
            }
        }
    }

}
