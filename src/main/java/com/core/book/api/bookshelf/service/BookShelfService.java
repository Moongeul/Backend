package com.core.book.api.bookshelf.service;

import com.core.book.api.bookshelf.dto.*;
import com.core.book.api.book.entity.Book;
import com.core.book.api.book.repository.BookRepository;
import com.core.book.api.bookshelf.entity.ReadBooks;
import com.core.book.api.bookshelf.entity.ReadBooksTag;
import com.core.book.api.bookshelf.entity.WishBooks;
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

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookShelfService {

    private final ReadBooksRepository readBooksRepository;
    private final WishBooksRepository wishBooksRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final ReadBooksTagRepository readBooksTagRepository;

    /*
     *
     * 책장 '조회' 메서드
     *
     */

    //'읽은 책' 책장 조회(list)
    public List<ReadBookshelfResponseDTO> showReadBooks(Long memberId) {

        // 책장 주인(회원)이 가진 책장 리스트 반환
        List<ReadBooks> readBooksList = readBooksRepository.findByMemberIdOrderByReadDateDesc(memberId);

        // ReadBooks 리스트를 ReadBookshelfResponseDTO 리스트로 변환
        return readBooksList.stream()
                .map(this::convertToReadBookshelfResponseDTO)
                .collect(Collectors.toList());
    }

    // ReadBooks를 ReadBookshelfResponseDTO로 변환하는 메서드
    private ReadBookshelfResponseDTO convertToReadBookshelfResponseDTO(ReadBooks readBooks) {
        // ReadBookshelfResponseDTO 객체 생성 (빌더 패턴 사용)
        return ReadBookshelfResponseDTO.builder()
                .bookImage(readBooks.getBook().getBook_image()) // 책 이미지
                .readDate(readBooks.getReadDate()) // 읽은 날짜
                .starRating(readBooks.getStar_rating()) // 평점
                .build();
    }

    //'읽고 싶은 책' 책장 조회(list)
    public List<WishBookshelfResponseDTO> showWishBooks(Long memberId){

        //책장 주인(회원)이 가진 책장 리스트 반환
        List<WishBooks> wishBooksList = wishBooksRepository.findByMemberId(memberId);

        return wishBooksList.stream()
                .map(this::convertToWishBookshelfResponseDTO)
                .collect(Collectors.toList());
    }

    // WishBooks를 WishBookshelfResponseDTO 변환하는 메서드
    private WishBookshelfResponseDTO convertToWishBookshelfResponseDTO(WishBooks wishBooks) {
        // WishBookshelfResponseDTO 객체 생성 (빌더 패턴 사용)
        return WishBookshelfResponseDTO.builder()
                .bookImage(wishBooks.getBook().getBook_image()) // 책 이미지
                .bookTitle(wishBooks.getBook().getTitle()) // 책 제목
                .reason(wishBooks.getReason()) // 읽고 싶은 이유
                .build();
    }

    /*
     *
     * 책장 '상세 조회' 메서드
     *
     */

    //'읽은 책' 책장 상세 정보 조회
    public ReadBooksDTO showReadBooksDetails(Long id){

        //id 에 따른 책장 단건 조회
        ReadBooks readBooks = readBooksRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.BOOKSHELF_INFO_NOTFOUND_EXCEPTION.getMessage()));

        ReadBooksTag readBooksTag = readBooks.getReadBooksTag();

        ReadBooksDTO.ReadBooksTagDTO tagDTO = ReadBooksDTO.ReadBooksTagDTO.builder()
                .tag1(readBooksTag.getTag1())
                .tag2(readBooksTag.getTag2())
                .tag3(readBooksTag.getTag3())
                .tag4(readBooksTag.getTag4())
                .tag5(readBooksTag.getTag5())
                .build();

        return ReadBooksDTO.builder()
                .readDate(readBooks.getReadDate())
                .starRating(readBooks.getStar_rating())
                .oneLineReview(readBooks.getOne_line_review())
                .readBooksTagDTO(tagDTO)
                .memberId(readBooks.getMember().getId())
                .build();
    }

    //'읽고 싶은 책' 책장 상세 정보 조회
    public WishBooksDTO showWishBooksDetails(Long id){

        //id 에 따른 책장 단건 조회
        WishBooks wishBooks = wishBooksRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.BOOKSHELF_INFO_NOTFOUND_EXCEPTION.getMessage()));

        return WishBooksDTO.builder()
                .reason(wishBooks.getReason())
                .memberId(wishBooks.getId())
                .build();
    }

    // 단일 ReadBooks 객체를 ReadBooksDTO로 변환
//    private ReadBooksDTO readBooksConvertToDTO(ReadBooks readBooks) {
//        ReadBooksTag readBooksTag = readBooks.getReadBooksTag();
//
//        ReadBooksDTO.ReadBooksTagDTO tagDTO = ReadBooksDTO.ReadBooksTagDTO.builder()
//                .tag1(readBooksTag.getTag1())
//                .tag2(readBooksTag.getTag2())
//                .tag3(readBooksTag.getTag3())
//                .tag4(readBooksTag.getTag4())
//                .tag5(readBooksTag.getTag5())
//                .build();
//
//        return ReadBooksDTO.builder()
//                .readDate(readBooks.getRead_date())
//                .starRating(readBooks.getStar_rating())
//                .oneLineReview(readBooks.getOne_line_review())
//                .readBooksTagDTO(tagDTO)
//                .memberId(readBooks.getMember().getId())
//                .build();
//    }

//    // 단일 WishBooks 객체를 WishBooksDTO로 변환
//    private WishBooksDTO wishBooksConvertToDTO(WishBooks wishBooks){
//
//        return WishBooksDTO.builder()
//                .reason(wishBooks.getReason())
//                .memberId(wishBooks.getId())
//                .build();
//    }

    /*
    *
    * 책장 '등록' 메서드
    *
    */

    // '읽은 책' 책장 등록 메서드
    @Transactional
    public void createReadBookshelf(ReadBookshelfRequestDTO readBookshelfDTO){

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
    public void createWishBookshelf(WishBookshelfRequestDTO wishBookshelfDTO){

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
