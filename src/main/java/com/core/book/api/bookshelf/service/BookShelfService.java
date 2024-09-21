package com.core.book.api.bookshelf.service;

import com.core.book.api.book.dto.BookDTO;
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
import com.core.book.common.exception.BadRequestException;
import com.core.book.common.exception.NotFoundException;
import com.core.book.common.response.ErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookShelfService {

    private final ReadBooksRepository readBooksRepository;
    private final WishBooksRepository wishBooksRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final ReadBooksTagRepository readBooksTagRepository;

    /*
     *
     * 책장 전체 '조회' 메서드
     *
     */

    /*
        '읽은 책' 전체 책장 조회(list)
    */
    public ReadBookshelfResponseDTO showReadBooks(Long memberId) {

        // 책장 주인(회원)이 가진 책장 리스트 반환
        List<ReadBooks> readBookList = readBooksRepository.findByMemberIdOrderByReadDateDesc(memberId);

        // 읽은 책 책장 응답 body 구성을 위한 DTO 리스트들 (초기화)
        List<ReadBookshelfResponseDTO.MonthlyInfoDTO> monthlyInfoDTOList = new ArrayList<>();
        List<ReadBookshelfResponseDTO.MonthlyInfoDTO.MonthlyReadBookDTO> monthlyReadBookDTOList = new ArrayList<>();

        // 책장 리스트가 비어있는지 검사
        if(!(readBookList.isEmpty())) { // 책장 리스트에 데이터가 있다면

            // 날짜, 책 권수 변수 (초기화)
            String monthlyDate = createMonthlyDate(readBookList.get(0)); // 첫 읽은 책 데이터의 읽은 날짜
            int monthlyReadBooksCnt = 0; // 날짜 별 읽은 책 권수

            // 읽은 날짜 별(년도-월) 책 분리
            for(int i = 0; i < readBookList.size(); i++) {
                ReadBooks readBooks = readBookList.get(i);

                /*
                    1. MonthlyReadBookDTO (책 정보) 리스트 만들기
                */

                // 현재 달 읽은 책 리스트(책 정보) 생성
                ReadBookshelfResponseDTO.MonthlyInfoDTO.MonthlyReadBookDTO monthlyReadBookDTO = convertToMonthlyReadBookDTO(readBooks);
                monthlyReadBookDTOList.add(monthlyReadBookDTO);

                // 현재 달 읽은 책 권수 증가
                monthlyReadBooksCnt++;

                /*
                    2. MonthlyInfoDTO (날짜 + 권수 + 책 정보) 리스트 만들기
                */

                // 다음 readBook 의 읽은 날짜가 변경되었는지(달이 지났는지) 확인
                if (i + 1 < readBookList.size()) { // 다음 요소가 존재하는지 확인
                    ReadBooks nextReadBook = readBookList.get(i + 1);
                    String nextMonthlyDate = createMonthlyDate(nextReadBook);

                    // 현재 책의 달과 다음 책의 달을 비교하여 조건 걸기
                    if (!monthlyDate.equals(nextMonthlyDate)) {
                        // 다음 책의 달이 바뀌었을 경우 : ReadBookshelfResponseDTO(읽은 책 전체 조회 응답 DTO) 데이터 생성 및 DTO 리스트에 추가
                        ReadBookshelfResponseDTO.MonthlyInfoDTO monthlyInfoDTO = createMonthlyInfoDTO(monthlyDate, monthlyReadBooksCnt, monthlyReadBookDTOList);
                        monthlyInfoDTOList.add(monthlyInfoDTO);

                        // 다음 책의 달로 갱신 + 읽은 책 권수 초기화 + monthlyReadBookDTOList 초기화
                        monthlyDate = nextMonthlyDate;
                        monthlyReadBooksCnt = 0;
                        monthlyReadBookDTOList = new ArrayList<>();
                    }
                } else {
                    // 마지막 책에 도달했을 때 마지막 책 달의 데이터들도 응답 DTO 리스트에 추가
                    ReadBookshelfResponseDTO.MonthlyInfoDTO monthlyInfoDTO = createMonthlyInfoDTO(monthlyDate, monthlyReadBooksCnt, monthlyReadBookDTOList);
                    monthlyInfoDTOList.add(monthlyInfoDTO);
                }
            }
        }

        return ReadBookshelfResponseDTO.builder()
                .totalBookCnt(readBookList.size())
                .monthlyInfoList(monthlyInfoDTOList)
                .build();
    }

    // MonthlyInfoDTO 생성 메서드
    private ReadBookshelfResponseDTO.MonthlyInfoDTO createMonthlyInfoDTO(String monthlyDate, int monthlyReadBooksCnt, List<ReadBookshelfResponseDTO.MonthlyInfoDTO.MonthlyReadBookDTO> monthlyReadBookList){
        return ReadBookshelfResponseDTO.MonthlyInfoDTO.builder()
                .date(monthlyDate)
                .monthlyBookCnt(monthlyReadBooksCnt)
                .monthlyReadBookList(monthlyReadBookList)
                .build();
    }

    // ReadBookList 의 각 요소를 MonthlyReadBookDTO 로 변환하는 메서드
    private ReadBookshelfResponseDTO.MonthlyInfoDTO.MonthlyReadBookDTO convertToMonthlyReadBookDTO(ReadBooks readBooks) {

        return ReadBookshelfResponseDTO.MonthlyInfoDTO.MonthlyReadBookDTO.builder()
                .isbn(readBooks.getBook().getIsbn()) // isbn
                .bookImage(readBooks.getBook().getBook_image()) // 책 이미지
                .starRating(readBooks.getStar_rating()) // 평점
                .title(readBooks.getBook().getTitle()) // 책 제목
                .readDate(readBooks.getReadDate()) // 읽은 날짜
                .build();
    }

    // 읽은 날짜의 년도-월(YYYY-M) 문자열 생성 메서드
    private String createMonthlyDate(ReadBooks readBooks){
        return readBooks.getReadDate().getYear() + "-" + readBooks.getReadDate().getMonthValue();
    }

    /*
        '읽고 싶은 책' 전체 책장 조회(list)
    */
    public WishBookshelfResponseDTO showWishBooks(Long memberId){

        // 책장 주인(회원)이 가진 '읽고 싶은 책' 책장 리스트 반환
        List<WishBooks> wishBookList = wishBooksRepository.findByMemberId(memberId);

        // wishBookList 의 각 요소를 WishBookshelfResponseDTO.wishBookDTO 로 변환
        List<WishBookshelfResponseDTO.wishBookDTO> wishBookDTOList = wishBookList.stream()
                .map(this::convertToWishBookDTO)
                .collect(Collectors.toList());

        // 읽고 싶은 책 전체 데이터를 담는 WishBookshelfResponseDTO 생성 후 반환
        return WishBookshelfResponseDTO.builder()
                .totalBookCnt(wishBookList.size())
                .wishBookList(wishBookDTOList)
                .build();
    }

    // wishBookList 의 각 요소를 WishBookshelfResponseDTO.wishBookDTO 로 변환하는 메서드
    private WishBookshelfResponseDTO.wishBookDTO convertToWishBookDTO(WishBooks wishBooks) {

        return WishBookshelfResponseDTO.wishBookDTO.builder()
                .isbn(wishBooks.getBook().getIsbn()) // isbn
                .bookImage(wishBooks.getBook().getBook_image()) // 책 이미지
                .bookTitle(wishBooks.getBook().getTitle()) // 책 제목
                .author(wishBooks.getBook().getAuthor()) // 저자
                .reason(wishBooks.getReason()) // 읽고 싶은 이유
                .build();
    }

    /*
     *
     * 책장 '상세 조회' 메서드
     *
     */

    // '읽은 책' 책장 상세 정보 조회
    public ReadBooksDTO showReadBooksDetails(Long id){

        //id 에 따른 책장 단건 조회
        ReadBooks readBooks = readBooksRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.BOOKSHELF_INFO_NOTFOUND_EXCEPTION.getMessage()));

        ReadBooksTag readBooksTag = readBooks.getReadBooksTag();

        ReadBooksDTO.ReadBooksTagDTO tagDTO = convertToReadBooksTagDTO(readBooksTag);

        return convertToReadBooksDTO(readBooks, tagDTO);
    }

    // ReadBooks를 ReadBooksDTO로 변환하는 메서드
    private ReadBooksDTO convertToReadBooksDTO(ReadBooks readBooks, ReadBooksDTO.ReadBooksTagDTO tagDTO){
        return ReadBooksDTO.builder()
                .readDate(readBooks.getReadDate())
                .starRating(readBooks.getStar_rating())
                .oneLineReview(readBooks.getOne_line_review())
                .readBooksTag(tagDTO)
                .memberId(readBooks.getMember().getId())
                .build();
    }

    // ReadBooksTag를 ReadBooksTagDTO로 변환하는 메서드
    private ReadBooksDTO.ReadBooksTagDTO convertToReadBooksTagDTO(ReadBooksTag readBooksTag){
        return ReadBooksDTO.ReadBooksTagDTO.builder()
                .tag1(readBooksTag.getTag1())
                .tag2(readBooksTag.getTag2())
                .tag3(readBooksTag.getTag3())
                .tag4(readBooksTag.getTag4())
                .tag5(readBooksTag.getTag5())
                .build();
    }

    // '읽고 싶은 책' 책장 상세 정보 조회
    public WishBooksDTO showWishBooksDetails(Long id){

        //id 에 따른 책장 단건 조회
        WishBooks wishBooks = wishBooksRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.BOOKSHELF_INFO_NOTFOUND_EXCEPTION.getMessage()));

        return convertToWishBooksDTO(wishBooks);
    }

    // wishBooks를 wishBooksDTO로 변환하는 메서드
    private WishBooksDTO convertToWishBooksDTO(WishBooks wishBooks){
        return WishBooksDTO.builder()
                .reason(wishBooks.getReason())
                .memberId(wishBooks.getId())
                .build();
    }

    /*
    *
    * 책장 '등록' 메서드
    *
    */

    // '읽은 책' 책장 등록 메서드
    @Transactional
    public void createReadBookshelf(ReadBookshelfRequestDTO readBookshelfDTO){

        String bookIsbn = readBookshelfDTO.getBook().getIsbn();
        Long memberId = readBookshelfDTO.getReadBooks().getMemberId();

        // 예외처리 : 이미 나의 책장에 등록된 책에 대하여 등록 불가
        checkDuplicateBookshelf(bookIsbn, memberId, true);

        // 책이 이미 BOOK DB에 존재한다면 -> DB 저장X / 없다면 -> DB 저장O
        Book book = saveBookIfNotExists(bookIsbn, readBookshelfDTO.getBook());

        // 예외처리 : 회원 존재 여부 확인
        Member member = getMemberById(memberId);

        // 선택된 태그 저장 (null 가능)
        ReadBooksTag savedTags = saveReadBooksTag(readBookshelfDTO.getReadBooks());

        // 책장 DB에 저장
        readBooksRepository.save(readBookshelfDTO.getReadBooks().toEntity(book, member, savedTags));
    }

    // '읽고 싶은 책' 책장 등록 메서드
    @Transactional
    public void createWishBookshelf(WishBookshelfRequestDTO wishBookshelfDTO){

        String bookIsbn = wishBookshelfDTO.getBook().getIsbn();
        Long memberId = wishBookshelfDTO.getWishBooks().getMemberId();

        // 예외처리 : 이미 나의 책장에 등록된 책에 대하여 등록 불가
        checkDuplicateBookshelf(bookIsbn, memberId, false);

        // 책이 이미 BOOK DB에 존재한다면 -> DB 저장X / 없다면 -> DB 저장O
        Book book = saveBookIfNotExists(bookIsbn, wishBookshelfDTO.getBook());

        // 예외처리 : 회원 존재 여부 확인
        Member member = getMemberById(memberId);

        // 책장 DB에 저장
        wishBooksRepository.save(wishBookshelfDTO.getWishBooks().toEntity(book, member));
    }

    // 중복 책장 등록 체크 메서드
    private void checkDuplicateBookshelf(String bookIsbn, Long memberId, boolean isReadBooks) {
        boolean existsBookshelf;
        if (isReadBooks) {
            existsBookshelf = readBooksRepository.existsByBookIsbnAndMemberId(bookIsbn, memberId);
        } else {
            existsBookshelf = wishBooksRepository.existsByBookIsbnAndMemberId(bookIsbn, memberId);
        }

        if (existsBookshelf) {
            throw new BadRequestException(ErrorStatus.NOT_ALLOW_DUPLICATE_BOOKSHELF.getMessage());
        }
    }

    // 책 존재 확인 및 저장 메서드
    private Book saveBookIfNotExists(String bookIsbn, BookDTO bookDTO){
        boolean existsBook = bookRepository.existsByIsbn(bookIsbn);
        if(!existsBook){
            // 책이 DB에 존재하지 않는 경우
            bookRepository.save(bookDTO.toEntity());
        }

        return bookRepository.findById(bookIsbn)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.BOOK_NOTFOUND_EXCEPTION.getMessage()));
    }

    // 회원 존재 확인 메서드
    private Member getMemberById(Long memberId){
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));
    }

    // '읽은 책' 태그 저장 메서드
    public ReadBooksTag saveReadBooksTag(ReadBooksDTO readBooksDTO){

        // 단, 태그가 하나도 입력되지 않았다면 저장 X
        if(readBooksDTO.getReadBooksTag().getTag1() == null
                && readBooksDTO.getReadBooksTag().getTag2() == null
                && readBooksDTO.getReadBooksTag().getTag3() == null
                && readBooksDTO.getReadBooksTag().getTag4() == null
                && readBooksDTO.getReadBooksTag().getTag5() == null){
            return null;
        }

        return readBooksTagRepository.save(readBooksDTO.getReadBooksTag().toEntity());
    }


    /*
     *
     * 책장 '상세 정보 수정' 메서드
     *
     */

    @Transactional
    public void updateReadBookshelf(ReadBooksDTO readBooksDTO, Long id){

        // 기존 책장 데이터 가져오기
        ReadBooks existingReadBooks = readBooksRepository.findById(id).
                orElseThrow(() -> new NotFoundException(ErrorStatus.BOOKSHELF_INFO_NOTFOUND_EXCEPTION.getMessage()));

        // 기존에 태그가 있으면 -> 수정 / 없으면 -> 새로 생성
        ReadBooksTag readBooksTag = existingReadBooks.getReadBooksTag();
        ReadBooksDTO.ReadBooksTagDTO tagDTO = readBooksDTO.getReadBooksTag();
        if (readBooksTag != null && tagDTO != null) {
            readBooksTag = ReadBooksTag.builder()
                    .id(readBooksTag.getId()) // 기존 태그의 ID 유지
                    .tag1(tagDTO.getTag1())
                    .tag2(tagDTO.getTag2())
                    .tag3(tagDTO.getTag3())
                    .tag4(tagDTO.getTag4())
                    .tag5(tagDTO.getTag5())
                    .build();
        } else if(readBooksTag == null && tagDTO != null){
            readBooksTag = tagDTO.toEntity();
            readBooksTagRepository.save(readBooksTag); // 새로운 태그 저장
        }

        // 수정된 ReadBooks 엔티티 생성
        ReadBooks updatedReadBooks = ReadBooks.builder()
                .id(existingReadBooks.getId()) // 기존 ID 유지
                .readDate(readBooksDTO.getReadDate())
                .star_rating(readBooksDTO.getStarRating())
                .one_line_review(readBooksDTO.getOneLineReview())
                .book(existingReadBooks.getBook()) // 기존 책 정보 유지
                .member(existingReadBooks.getMember()) // 기존 회원 정보 유지
                .readBooksTag(readBooksTag)
                .build();

        // 수정된 엔티티 저장
        readBooksRepository.save(updatedReadBooks);
    }

    @Transactional
    public void updateWishBookshelf(WishBooksDTO wishBooksDTO, Long id){

        // 기존 책장 데이터 가져오기
        WishBooks existingwishBooks = wishBooksRepository.findById(id).
                orElseThrow(() -> new NotFoundException(ErrorStatus.BOOKSHELF_INFO_NOTFOUND_EXCEPTION.getMessage()));

        // 수정된 WishBooks 엔티티 생성
        WishBooks updatedWishBooks = WishBooks.builder()
                .id(existingwishBooks.getId())
                .reason(wishBooksDTO.getReason())
                .book(existingwishBooks.getBook())
                .member(existingwishBooks.getMember())
                .build();

        wishBooksRepository.save(updatedWishBooks);
    }

    /*
     *
     * 책장 '삭제' 메서드
     *
     */

    @Transactional
    public void deleteReadBookshelf(Long id){
        ReadBooks readBooks = readBooksRepository.findById(id).
                orElseThrow(() -> new NotFoundException(ErrorStatus.BOOKSHELF_INFO_NOTFOUND_EXCEPTION.getMessage()));

        readBooksRepository.delete(readBooks);
    }

    @Transactional
    public void deleteWishBookshelf(Long id){
        WishBooks wishBooks = wishBooksRepository.findById(id).
                orElseThrow(() -> new NotFoundException(ErrorStatus.BOOKSHELF_INFO_NOTFOUND_EXCEPTION.getMessage()));

        wishBooksRepository.delete(wishBooks);
    }

    /*
     *
     * 책장 - 읽고 싶은 책 -> 읽은 책 '이동' 메서드
     *
     */
    @Transactional
    public void shiftBookshelf(ReadBooksDTO readBooksDTO, Long id){

        WishBooks wishBooks = wishBooksRepository.findById(id).
                orElseThrow(() -> new NotFoundException(ErrorStatus.BOOKSHELF_NOTFOUND_EXCEPTION.getMessage()));

        /* (1) 읽은 책 책장에 등록 */

        // 책 정보 가져오기
        BookDTO bookDTO = convertToBookDTO(wishBooks.getBook());

        // ReadBookshelfRequestDTO 만들기
        ReadBookshelfRequestDTO readBookshelfRequestDTO = ReadBookshelfRequestDTO.builder()
                .book(bookDTO)
                .readBooks(readBooksDTO)
                .build();

        // '읽은 책' 책장 등록
        createReadBookshelf(readBookshelfRequestDTO);

        /* (2) 읽고 싶은 책 책장에서 삭제 */
        deleteWishBookshelf(id);
    }

    // book을 BookDTO로 변경하는 메서드
    private BookDTO convertToBookDTO(Book book){
        return BookDTO.builder()
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .image(book.getBook_image())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .description(book.getDescription())
                .pubdate(book.getPubdate())
                .bookTag(book.getBookTag())
                .build();
    }

}
