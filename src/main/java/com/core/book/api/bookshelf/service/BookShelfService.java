package com.core.book.api.bookshelf.service;

import com.core.book.api.bookshelf.dto.ReadBooksDTO;
import com.core.book.api.book.entity.Book;
import com.core.book.api.book.repository.BookRepository;
import com.core.book.api.book.service.BookService;
import com.core.book.api.bookshelf.dto.ReadBookshelfDTO;
import com.core.book.api.bookshelf.dto.WishBookshelfDTO;
import com.core.book.api.bookshelf.entity.ReadBooksTag;
import com.core.book.api.bookshelf.repository.ReadBooksRepository;
import com.core.book.api.bookshelf.repository.ReadBooksTagRepository;
import com.core.book.api.bookshelf.repository.WishBooksRepository;
import com.core.book.api.member.entity.Member;
import com.core.book.api.member.repository.MemberRepository;
import com.core.book.common.exception.NotFoundException;
import com.core.book.common.response.ErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookShelfService {

    private final ReadBooksRepository readBooksRepository;
    private final WishBooksRepository wishBooksRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final ReadBooksTagRepository readBooksTagRepository;

    // '읽은 책' 책장 등록 메서드
    @Transactional
    public void createReadBookshelf(ReadBookshelfDTO readBookshelfDTO){

        // 선택된 책 DB에 저장
        Book savedbook = bookRepository.save(readBookshelfDTO.getBookDto().toEntity());

        // 책, 회원 데이터 유무 확인
        Book book = bookRepository.findById(savedbook.getId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.BOOK_NOTFOUND_EXCEPTION.getMessage()));
        Member member = memberRepository.findById(readBookshelfDTO.getReadBooksDTO().getMemberId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        // (있으면) 선택된 태그 저장
        ReadBooksTag savedTags = saveReadBooksTag(readBookshelfDTO.getReadBooksDTO());

        // 책장 DB에 저장
        readBooksRepository.save(readBookshelfDTO.getReadBooksDTO().toEntity(book, member, savedTags));
    }

    // '읽은 책' 태그 저장 메서드
    public ReadBooksTag saveReadBooksTag(ReadBooksDTO readBooksDTO){

        // 단, 태그가 하나도 입력되지 않았다면 저장 X
        if(readBooksDTO.getReadBooksTagDTO().getTag1() == null
                && readBooksDTO.getReadBooksTagDTO().getTag2() == null
                && readBooksDTO.getReadBooksTagDTO().getTag3() == null
                && readBooksDTO.getReadBooksTagDTO().getTag4() == null
                && readBooksDTO.getReadBooksTagDTO().getTag5() == null){
            return null;
        }

        return readBooksTagRepository.save(readBooksDTO.getReadBooksTagDTO().toEntity());
    }

    // '읽고 싶은 책' 책장 등록 메서드
    @Transactional
    public void createWishBookshelf(WishBookshelfDTO wishBookshelfDTO){

        // 선택된 책 DB에 저장
        Book savedbook = bookRepository.save(wishBookshelfDTO.getBookDto().toEntity());

        // 책, 회원 데이터 유무 확인
        Book book = bookRepository.findById(savedbook.getId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.BOOK_NOTFOUND_EXCEPTION.getMessage()));
        Member member = memberRepository.findById(wishBookshelfDTO.getWishBooksDTO().getMemberId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        // 책장 DB에 저장
        wishBooksRepository.save(wishBookshelfDTO.getWishBooksDTO().toEntity(book, member));
    }
}
