package com.core.book.api.book.service;

import com.core.book.api.book.dto.BookDTO;
import com.core.book.api.book.dto.BookResponseDTO;
import com.core.book.api.book.dto.ChannelDTO;
import com.core.book.api.book.dto.ResultDTO;
import com.core.book.common.exception.NotFoundException;
import com.core.book.common.response.ErrorStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
@Service
public class BookService {

    @Value("${naver-client-id}")
    private String clientId;

    @Value("${naver-client-secret}")
    private String clientSecret;

    public BookResponseDTO book(String text, int page, int size){

        int start = (page - 1) * size + 1; //검색 시작 위치 변수
        boolean isLast = false; // 마지막 페이지 여부 (true = 마지막 페이지)

        // String apiURL
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/search/book.xml")
                .queryParam("query", text) //검색어
                .queryParam("display", size) //한 번에 표시할 검색 결과 개수
                .queryParam("start", start) //검색 시작 위치
                .queryParam("sort", "sim") //검색 결과 정렬 방법
                .encode()
                .build()
                .toUri();

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

        ResultDTO resultDTO = channelDTO.getChannel();

        // 예외 처리 - "더 이상 검색 결과가 없습니다."
        if(resultDTO.getTotal() != 0 && resultDTO.getTotal() < start){
            throw new NotFoundException(ErrorStatus.BOOK_NO_MORE_FOUND_EXCEPTION.getMessage());
        }

        // 책 정보 데이터 담긴 List 변수 : bookDTOS
        List<BookDTO> bookDTOs = Optional.of(resultDTO)
                .map(ResultDTO::getItems)
                .orElse(Collections.emptyList());

        // bookDTOs를 BookResponseDTO.BookDTO로 변환
        List<BookResponseDTO.BookDTO> responseBookDTOs = bookDTOs.stream()
                .map(bookDTO -> BookResponseDTO.BookDTO.builder()
                        .isbn(bookDTO.getIsbn())
                        .title(bookDTO.getTitle())
                        .image(bookDTO.getImage())
                        .author(bookDTO.getAuthor())
                        .publisher(bookDTO.getPublisher())
                        .pubdate(bookDTO.getPubdate())
                        .build())
                .collect(Collectors.toList());

        // 마지막 페이지 여부 검사
        if(start + size >= resultDTO.getTotal()){
            isLast = true;
        }

        // BookResponseDTO 반환
        return BookResponseDTO.builder()
                .totalSize(resultDTO.getTotal())
                .page(page)
                .bookList(responseBookDTOs)
                .isLast(isLast)
                .build();
    }
}
