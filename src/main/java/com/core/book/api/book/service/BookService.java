package com.core.book.api.book.service;

import com.core.book.api.article.entity.Article;
import com.core.book.api.article.repository.ArticleRepository;
import com.core.book.api.book.constant.BookTag;
import com.core.book.api.book.dto.*;
import com.core.book.api.book.entity.Book;
import com.core.book.api.book.repository.BookRepository;
import com.core.book.api.book.repository.UserBookTagRepository;
import com.core.book.common.exception.NotFoundException;
import com.core.book.common.response.ErrorStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final UserBookTagRepository userBookTagRepository;
    private final ArticleRepository articleRepository;

    @Value("${naver-client-id}")
    private String clientId;

    @Value("${naver-client-secret}")
    private String clientSecret;

    public BookResponseDTO bookSearch(String text, int page, int size){

        int start = (page - 1) * size + 1; //검색 시작 위치 변수
        boolean isLast = false; // 마지막 페이지 여부 (true = 마지막 페이지)

        // 외부 도서 API 요청
        URI uri = uriComponentBuild(text, null, page, size);
        ResultDTO resultDTO = fetchBookData(uri);

        // 예외 처리 - "더 이상 검색 결과가 없습니다."
        if(resultDTO.getTotal() != 0 && resultDTO.getTotal() < start){
            throw new NotFoundException(ErrorStatus.BOOK_NO_MORE_FOUND_EXCEPTION.getMessage());
        }

        // 책 정보 데이터 담긴 List 변수 : bookList
        List<BookDTO> bookList = Optional.of(resultDTO)
                .map(ResultDTO::getItems)
                .orElse(Collections.emptyList());

        // bookList를 BookInfoDTO로 변환
        List<BookInfoDTO> BookInfoList = bookList.stream()
                .map(this::convertFromBookDTOToBookInfoDTO)
                .collect(Collectors.toList());

        // 마지막 페이지 여부 검사
        if(start + size >= resultDTO.getTotal()){
            isLast = true;
        }

        // BookResponseDTO 반환
        return BookResponseDTO.builder()
                .totalSize(resultDTO.getTotal())
                .page(page)
                .bookList(BookInfoList)
                .isLast(isLast)
                .build();
    }

    public BookInfoDetailDTO bookInfo(String isbn){

        BookDTO bookDTO;

        float ratingAverage = 0.0F;
        List<String> tagList = new ArrayList<>();
        List<ReviewPreviewDTO> reviewPreviewList = new ArrayList<>();

        // BOOK DB에서 책 데이터 찾기
        if(bookRepository.existsByIsbn(isbn)){
            
            Book book = bookRepository.findById(isbn)
                    .orElseThrow(() -> new NotFoundException(ErrorStatus.BOOK_NOTFOUND_EXCEPTION.getMessage()));

            bookDTO = convertFromBookToBookInfoDTO(book);

            // 평균 평점
            ratingAverage = book.getRatingAverage();

            // Best 5 태그
            List<Integer> tagIds = userBookTagRepository.findTop5TagsByIsbn(isbn, PageRequest.of(0, 5));
            for (Integer id : tagIds) {
                tagList.add(BookTag.fromId(id).getDescription());
            }

            // 리뷰 (표시정보 : profile_image, nickname, article_type, content)
            List<Article> articleList = articleRepository.findByBookIsbnOrderByCreatedAtDesc(isbn, PageRequest.of(0, 5));
            for(Article article : articleList){
                ReviewPreviewDTO reviewPreview = ReviewPreviewDTO.builder()
                        .profileImage(article.getMember().getImageUrl())
                        .nickname(article.getMember().getNickname())
                        .articleType(article.getType())
                        .content(article.getContent())
                        .build();
                reviewPreviewList.add(reviewPreview);
            }

        }
        else{ // (DB에 없다면) 외부 도서 API에 데이터 요청

            // 외부 도서 API 요청
            URI uri = uriComponentBuild(null, isbn, 1, 10);
            ResultDTO resultDTO = fetchBookData(uri);

            // 검색 결과 데이터 변환
            List<BookDTO> bookList = Optional.of(resultDTO)
                    .map(ResultDTO::getItems)
                    .orElse(Collections.emptyList());
            bookDTO = bookList.isEmpty() ? null : bookList.get(0); // List의 첫 번째 값 가져와 Book entity로 변환
        }

        if(bookDTO != null){
            return convertFromBookDTOToBookInfoDetailDTO(bookDTO, ratingAverage, tagList, reviewPreviewList);
        } else {
            return null;
        }
    }

    // 외부 도서 API 에 요청할 URI 생성
    private URI uriComponentBuild(String text, String isbn, int page, int size){

        int start = (page - 1) * size + 1; //검색 시작 위치 변수
        String searchType = "book.xml";
        if(isbn != null){
            searchType = "book_adv.xml";
        }

        return UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/search/" + searchType)
                .queryParam("query", text) //검색어
                .queryParam("display", size) //한 번에 표시할 검색 결과 개수
                .queryParam("start", start) //검색 시작 위치
                .queryParam("sort", "sim") //검색 결과 정렬 방법
                .queryParam("d_isbn", isbn) // 검색할 ISBN
                .encode()
                .build()
                .toUri();
    }

    // 도서 데이터 요청 & Xml 파싱 후 resultDTO 반환
    private ResultDTO fetchBookData(URI uri){

        // Spring 요청 제공 클래스
        RequestEntity<Void> req = RequestEntity.get(uri)
                .header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret)
                .build();

        // Spring 제공 RestTemplate
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> resp = restTemplate.exchange(req, String.class); //응답 본문 String 타입으로 변환

        // Xml 파싱 (Xml 문자열을 객체로 만듦, 문서화)
        XmlMapper xmlMapper = new XmlMapper();
        ChannelDTO channelDTO = null;

        // XmlMapper를 사용해서 XML 데이터를 처리하고 있지만, Jackson의 예외 처리는 여전히 JSON 기반 예외 처리 구조를 따름
        try {
            channelDTO = xmlMapper.readValue(resp.getBody(), ChannelDTO.class);
        } catch (JsonMappingException e) {
            log.error("JsonMappingException occurred while reading value", e);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException occurred while reading value", e);
        }

        if(channelDTO == null){
            throw new NotFoundException(ErrorStatus.FAIL_REQUEST_BOOK_INFO.getMessage());
        }

        return channelDTO.getChannel();
    }

    // Book을 BookInfoDTO로 변환
    private BookDTO convertFromBookToBookInfoDTO(Book book){
        return BookDTO.builder()
                .isbn(book.getIsbn())
                .image(book.getBookImage())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .pubdate(book.getPubdate())
                .description(book.getDescription())
                .build();
    }

    // BookDTO를 BookInfoDTO로 변환
    private BookInfoDTO convertFromBookDTOToBookInfoDTO(BookDTO bookDTO){
        return BookInfoDTO.builder()
                .isbn(bookDTO.getIsbn())
                .image(bookDTO.getImage())
                .title(bookDTO.getTitle())
                .author(bookDTO.getAuthor())
                .publisher(bookDTO.getPublisher())
                .pubdate(bookDTO.getPubdate())
                .description(bookDTO.getDescription())
                .build();
    }

    // BookDTO를 BookInfoDetailDTO로 변환
    private BookInfoDetailDTO convertFromBookDTOToBookInfoDetailDTO(BookDTO bookDTO, float ratingAverage, List<String> tagList, List<ReviewPreviewDTO> reviewPreviewList){
        return BookInfoDetailDTO.builder()
                .isbn(bookDTO.getIsbn())
                .image(bookDTO.getImage())
                .title(bookDTO.getTitle())
                .author(bookDTO.getAuthor())
                .publisher(bookDTO.getPublisher())
                .pubdate(bookDTO.getPubdate())
                .description(bookDTO.getDescription())
                .ratingAverage(ratingAverage)
                .tagList(tagList)
                .reviewPreviewList(reviewPreviewList)
                .build();
    }
}
