package com.core.book.api.bookshelf.service;

import com.core.book.api.book.constant.BookTag;
import com.core.book.api.book.dto.BookInfoDTO;
import com.core.book.api.book.dto.UserBookTagDTO;
import com.core.book.api.book.entity.UserBookTag;
import com.core.book.api.book.repository.UserBookTagRepository;
import com.core.book.api.book.service.UserBookTagService;
import com.core.book.api.bookshelf.dto.*;
import com.core.book.api.book.entity.Book;
import com.core.book.api.book.repository.BookRepository;
import com.core.book.api.bookshelf.entity.ReadBooks;
import com.core.book.api.bookshelf.entity.WishBooks;
import com.core.book.api.bookshelf.repository.ReadBooksRepository;
import com.core.book.api.bookshelf.repository.WishBooksRepository;
import com.core.book.api.member.entity.Member;
import com.core.book.api.member.repository.MemberRepository;
import com.core.book.common.exception.BadRequestException;
import com.core.book.common.exception.NotFoundException;
import com.core.book.common.response.ErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final UserBookTagRepository userBookTagRepository;
    private final UserBookTagService userBookTagService;

    // 회원 객체 가져오기 메서드
    private Member getMemberById(Long memberId){
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));
    }

    /*
     *
     * 책장 전체 '조회' 메서드
     *
     */

    /*
        '읽은 책' 전체 책장 조회(list)
    */
    public ReadBookshelfResponseDTO showReadBooks(Long userId, int page, int size, int filterNum) {

        /*
         *  filter
         *  1: 전체보기(최신순), 2: 오래된 순, 3: 평점 높은 순, 4: 평점 낮은 순
         */

        // filterNum = 1 or 2 -> "readDate"로 정렬 / filterNum = 3 or 4 -> "starRating"으로 정렬
        String filter = (filterNum <= 2) ? "readDate" : "starRating";

        // filterNum = 1 or 3 -> "ASC"으로 정렬 / filterNum = 2 or 4 -> "ASC"로 정렬
        Sort.Direction direction = (filterNum % 2 == 0) ? Sort.Direction.ASC : Sort.Direction.DESC;

        // Pageable 객체 생성
        // Sort - filter 값 우선 정렬 후 id 값으로 정렬됨
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, filter, "id"));

        // 페이징된 결과물 반환
        Page<ReadBooks> readBookPage = readBooksRepository.findByMemberId(userId, pageable);

        /*
         *  전체 조회 데이터 가져오기
         */

        // 책장 주인(회원)이 가진 책장 리스트 반환
        List<ReadBooks> readBookList = readBookPage.getContent();

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
                .totalBookCnt(readBookPage.getTotalElements())
                .monthlyInfoList(monthlyInfoDTOList)
                .page(page)
                .isLast(readBookPage.isLast())
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
                .id(readBooks.getId())
                .isbn(readBooks.getBook().getIsbn()) // isbn
                .bookImage(readBooks.getBook().getBookImage()) // 책 이미지
                .rating(readBooks.getRating()) // 평점
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
    public WishBookshelfResponseDTO showWishBooks(Long userId, int page, int size){

        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));

        // 페이징된 결과물 반환
        Page<WishBooks> wishBookPage = wishBooksRepository.findByMemberId(userId, pageable);

        // 책장 주인(회원)이 가진 책장 리스트 반환
        List<WishBooks> wishBookList = wishBookPage.getContent();

        // wishBookList 의 각 요소를 WishBookshelfResponseDTO.wishBookDTO 로 변환
        List<WishBookshelfResponseDTO.wishBookDTO> wishBookDTOList = wishBookList.stream()
                .map(this::convertToWishBookDTO)
                .collect(Collectors.toList());

        // 읽고 싶은 책 전체 데이터를 담는 WishBookshelfResponseDTO 생성 후 반환
        return WishBookshelfResponseDTO.builder()
                .totalBookCnt(wishBookPage.getTotalElements())
                .wishBookList(wishBookDTOList)
                .page(page)
                .isLast(wishBookPage.isLast())
                .build();
    }

    // wishBookList 의 각 요소를 WishBookshelfResponseDTO.wishBookDTO 로 변환하는 메서드
    private WishBookshelfResponseDTO.wishBookDTO convertToWishBookDTO(WishBooks wishBooks) {

        return WishBookshelfResponseDTO.wishBookDTO.builder()
                .id(wishBooks.getId()) // '읽고 싶은 책' 책장 데이터의 id
                .isbn(wishBooks.getBook().getIsbn()) // isbn
                .bookImage(wishBooks.getBook().getBookImage()) // 책 이미지
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

        List<UserBookTag> userBookTags = userBookTagRepository.findByReadBooks(readBooks);

        // 태그가 입력된 것이 없을 경우, null 반환
        if(userBookTags.isEmpty()){
            return convertToReadBooksDTO(readBooks, null);
        }

        List<UserBookTagDTO> tagDTO = convertToReadBooksTagDTO(userBookTags);

        return convertToReadBooksDTO(readBooks, tagDTO);
    }

    // ReadBooks를 ReadBooksDTO로 변환하는 메서드
    private ReadBooksDTO convertToReadBooksDTO(ReadBooks readBooks, List<UserBookTagDTO> tagDTO){
        return ReadBooksDTO.builder()
                .readDate(readBooks.getReadDate())
                .rating(readBooks.getRating())
                .oneLineReview(readBooks.getOneLineReview())
                .userBookTagList(tagDTO)
                .build();
    }

    // ReadBooksTag를 ReadBooksTagDTO로 변환하는 메서드
    private List<UserBookTagDTO> convertToReadBooksTagDTO(List<UserBookTag> userBookTags) {
        return userBookTags.stream()
                .map(userBookTag -> UserBookTagDTO.builder()
                        .tagId(userBookTag.getId())
                        .tag(BookTag.fromId(userBookTag.getTag()).getDescription())
                        .build())
                .collect(Collectors.toList());
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
                .build();
    }

    /*
     *
     * 책장 '등록' 메서드
     *
     */

    // '읽은 책' 책장 등록 메서드
    @Transactional
    public void createReadBookshelf(ReadBookshelfRequestDTO readBookshelfDTO, Long userId){

        String bookIsbn = readBookshelfDTO.getBookInfo().getIsbn();

        // 예외처리 : 이미 나의 책장에 등록된 책에 대하여 등록 불가
        checkDuplicateBookshelf(bookIsbn, userId, true);

        // 책이 이미 BOOK DB에 존재한다면 -> DB 저장X / 없다면 -> DB 저장O
        Book book = saveBookIfNotExists(bookIsbn, readBookshelfDTO.getBookInfo());

        // 책장 저장을 위한 회원 객체 가져오기
        Member member = getMemberById(userId);

        // 책장 DB에 저장
        ReadBooks readBooks = readBooksRepository.save(readBookshelfDTO.getReadBooks().toEntity(book, member));

        // 선택된 태그 저장 (null 가능)
        List<UserBookTagDTO> tagList = readBookshelfDTO.getReadBooks().getUserBookTagList();
        userBookTagService.updateUserBookTag(tagList, book, readBooks, null);

        // BOOK rating_average 갱신
        updateRatingAverage(book, readBookshelfDTO.getReadBooks().getRating());

    }

    // BOOK rating_average 갱신 메서드
    public void updateRatingAverage(Book book, double new_rating){

        // 해당 책의 평균 평점(rating_average) 새로 계산 및 rating_count(평점 개수) + 1
        // 계산 공식: new_rating_average = (rating_average * rating_count + rating) / rating_count + 1 )
        float new_rating_average = (float) ((book.getRatingAverage() * book.getRatingCount() + new_rating) / (book.getRatingCount() + 1));
        new_rating_average = (float) (Math.round(new_rating_average * 100) / 100.0);

        Book updatedBook = book.toBuilder()
                .ratingAverage(new_rating_average)
                .ratingCount(book.getRatingCount() + 1)
                .build();

        bookRepository.save(updatedBook);
    }


    // '읽고 싶은 책' 책장 등록 메서드
    @Transactional
    public void createWishBookshelf(WishBookshelfRequestDTO wishBookshelfDTO, Long userId){

        String bookIsbn = wishBookshelfDTO.getBookInfo().getIsbn();

        // 예외처리 : 이미 나의 책장에 등록된 책에 대하여 등록 불가
        checkDuplicateBookshelf(bookIsbn, userId, false);

        // 책이 이미 BOOK DB에 존재한다면 -> DB 저장X / 없다면 -> DB 저장O
        Book book = saveBookIfNotExists(bookIsbn, wishBookshelfDTO.getBookInfo());

        // 책장 저장을 위한 회원 객체 가져오기
        Member member = getMemberById(userId);

        // 책장 DB에 저장
        wishBooksRepository.save(wishBookshelfDTO.getWishBooks().toEntity(book, member));
    }

    // 중복 책장 등록 체크 메서드
    private void checkDuplicateBookshelf(String bookIsbn, Long userId, boolean isReadBooks) {
        boolean existsBookshelf;
        if (isReadBooks) {
            existsBookshelf = readBooksRepository.existsByBookIsbnAndMemberId(bookIsbn, userId);
        } else {
            existsBookshelf = wishBooksRepository.existsByBookIsbnAndMemberId(bookIsbn, userId);
        }

        if (existsBookshelf) {
            throw new BadRequestException(ErrorStatus.NOT_ALLOW_DUPLICATE_BOOKSHELF.getMessage());
        }
    }

    // 책 존재 확인 및 저장 메서드
    private Book saveBookIfNotExists(String bookIsbn, BookInfoDTO bookInfoDTO){
        boolean existsBook = bookRepository.existsByIsbn(bookIsbn);
        if(!existsBook){
            // 책이 DB에 존재하지 않는 경우
            bookRepository.save(bookInfoDTO.toEntity());
        }

        return bookRepository.findById(bookIsbn)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.BOOK_NOTFOUND_EXCEPTION.getMessage()));
    }


    /*
     *
     * 책장 '상세 정보 수정' 메서드
     *
     */

    @Transactional
    public void updateReadBookshelf(ReadBooksDTO readBooksDTO, Long id, Long userId){

        // 기존 책장 데이터 가져오기
        ReadBooks existingReadBooks = readBooksRepository.findById(id).
                orElseThrow(() -> new NotFoundException(ErrorStatus.BOOKSHELF_INFO_NOTFOUND_EXCEPTION.getMessage()));

        double old_rating = existingReadBooks.getRating();

        // 예외처리: 책장 소유자와 수정 요청자가 다른 경우
        if(!existingReadBooks.getMember().getId().equals(userId)){
            throw new BadRequestException(ErrorStatus.BOOKSHELF_MODIFY_NOT_SAME_USER_EXCEPTION.getMessage());
        }

        // 태그 수정
        List<UserBookTagDTO> tagList = readBooksDTO.getUserBookTagList();
        userBookTagService.updateUserBookTag(tagList, existingReadBooks.getBook(), existingReadBooks, null);

        // 수정된 ReadBooks 엔티티 생성
        ReadBooks updatedReadBooks = ReadBooks.builder()
                .id(existingReadBooks.getId()) // 기존 ID 유지
                .readDate(readBooksDTO.getReadDate())
                .rating(readBooksDTO.getRating())
                .oneLineReview(readBooksDTO.getOneLineReview())
                .book(existingReadBooks.getBook()) // 기존 책 정보 유지
                .member(existingReadBooks.getMember()) // 기존 회원 정보 유지
                .build();

        // 수정된 엔티티 저장
        readBooksRepository.save(updatedReadBooks);

        // 평균 평점 수정 (단, rating_count 는 오르지 않음)
        Book book = bookRepository.findById(existingReadBooks.getBook().getIsbn()).
                orElseThrow(() -> new NotFoundException(ErrorStatus.BOOK_NOTFOUND_EXCEPTION.getMessage()));

        modifyRatingAverage(book, readBooksDTO.getRating(), old_rating);

    }

    // BOOK rating_average 갱신 메서드
    public void modifyRatingAverage(Book book, double new_rating, double old_rating){

        // 새 점수들의 합 = (기존 점수들의 합) - (바뀌기 이전 평점) + (바뀐 평점)
        float new_rating_sum = (float) ((book.getRatingAverage() * book.getRatingCount()) - old_rating + new_rating);
        float new_rating_average = new_rating_sum / book.getRatingCount();
        new_rating_average = (float) (Math.round(new_rating_average * 100) / 100.0);

        Book updatedBook = book.toBuilder()
                .ratingAverage(new_rating_average)
                .build();

        bookRepository.save(updatedBook);
    }

    // '읽고 싶은 책' 책장 수정
    @Transactional
    public void updateWishBookshelf(WishBooksDTO wishBooksDTO, Long id, Long userId){

        // 기존 책장 데이터 가져오기
        WishBooks existingwishBooks = wishBooksRepository.findById(id).
                orElseThrow(() -> new NotFoundException(ErrorStatus.BOOKSHELF_INFO_NOTFOUND_EXCEPTION.getMessage()));

        // 예외처리: 책장 소유자와 수정 요청자가 다른 경우
        if(!existingwishBooks.getMember().getId().equals(userId)){
            throw new BadRequestException(ErrorStatus.BOOKSHELF_MODIFY_NOT_SAME_USER_EXCEPTION.getMessage());
        }

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
    public void deleteReadBookshelf(Long id, Long userId){
        ReadBooks readBooks = readBooksRepository.findById(id).
                orElseThrow(() -> new NotFoundException(ErrorStatus.BOOKSHELF_INFO_NOTFOUND_EXCEPTION.getMessage()));

        // 예외처리: 책장 소유자와 삭제 요청자가 다른 경우
        if(!readBooks.getMember().getId().equals(userId)){
            throw new BadRequestException(ErrorStatus.BOOKSHELF_DELETE_NOT_SAME_USER_EXCEPTION.getMessage());
        }

        /* UserBookTag 삭제 */
        // 해당 회원과 책에 해당하는 UserBookTag 조회
        List<UserBookTag> userBookTags = userBookTagRepository.findByReadBooks(readBooks);

        // 조회된 UserBookTag가 있으면 삭제
        if(!userBookTags.isEmpty()){
            // 튜플 삭제
            userBookTagRepository.deleteAll(userBookTags);
        }

        readBooksRepository.delete(readBooks);

        // 평균 평점 갱신
        updateRatingAverageAfterDeletion(readBooks.getBook(), readBooks.getRating());
    }

    // 평점이 삭제된 경우 평균 평점 갱신
    public void updateRatingAverageAfterDeletion(Book book, double removed_rating) {
        int ratingCount = book.getRatingCount();

        if (ratingCount <= 1) {
            // 평점이 1개뿐이면 삭제 후 평균을 0으로 설정
            book = book.toBuilder()
                    .ratingAverage(0.0f)
                    .ratingCount(0)
                    .build();
        } else {
            // 새로운 평균 평점 계산
            float new_rating_average = (float) ((book.getRatingAverage() * ratingCount - removed_rating) / (ratingCount - 1));
            new_rating_average = (float) (Math.round(new_rating_average * 100) / 100.0);

            book = book.toBuilder()
                    .ratingAverage(new_rating_average)
                    .ratingCount(ratingCount - 1)
                    .build();
        }

        bookRepository.save(book);
    }

    @Transactional
    public void deleteWishBookshelf(Long id, Long userId){
        WishBooks wishBooks = wishBooksRepository.findById(id).
                orElseThrow(() -> new NotFoundException(ErrorStatus.BOOKSHELF_INFO_NOTFOUND_EXCEPTION.getMessage()));

        // 예외처리: 책장 소유자와 삭제 요청자가 다른 경우
        if(!wishBooks.getMember().getId().equals(userId)){
            throw new BadRequestException(ErrorStatus.BOOKSHELF_DELETE_NOT_SAME_USER_EXCEPTION.getMessage());
        }

        wishBooksRepository.delete(wishBooks);
    }

    /*
     *
     * 책장 - 읽고 싶은 책 -> 읽은 책 '이동' 메서드
     *
     */
    @Transactional
    public void shiftBookshelf(ReadBooksDTO readBooksDTO, Long id, Long userId){

        WishBooks wishBooks = wishBooksRepository.findById(id).
                orElseThrow(() -> new NotFoundException(ErrorStatus.BOOKSHELF_NOTFOUND_EXCEPTION.getMessage()));

        /* (1) 읽은 책 책장에 등록 */

        // 책 정보 가져오기
        BookInfoDTO bookInfoDTO = convertToBookInfoDTO(wishBooks.getBook());

        // ReadBookshelfRequestDTO 만들기
        ReadBookshelfRequestDTO readBookshelfRequestDTO = ReadBookshelfRequestDTO.builder()
                .bookInfo(bookInfoDTO)
                .readBooks(readBooksDTO)
                .build();

        // '읽은 책' 책장 등록
        createReadBookshelf(readBookshelfRequestDTO, userId);

        /* (2) 읽고 싶은 책 책장에서 삭제 */
        deleteWishBookshelf(id, userId);
    }

    // book을 BookDTO로 변경하는 메서드
    private BookInfoDTO convertToBookInfoDTO(Book book){
        return BookInfoDTO.builder()
                .isbn(book.getIsbn())
                .image(book.getBookImage())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .pubdate(book.getPubdate())
                .description(book.getDescription())
                .build();
    }

}
