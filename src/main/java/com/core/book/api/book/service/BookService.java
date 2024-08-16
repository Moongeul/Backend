package com.core.book.api.book.service;

import com.core.book.api.book.dto.BookDto;
import com.core.book.api.book.dto.ResultDto;
import com.core.book.api.book.entity.Book;
import com.core.book.api.book.repository.BookRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Value("${naver-client-id}")
    private String clientId;

    @Value("${naver-client-secret}")
    private String clientSecret;

    public Iterable<Book> book(String text){

        //String apiURL
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/search/book.json")
                .queryParam("query", text)
                .queryParam("display", 10)
                .queryParam("start", 1)
                .queryParam("sort", "sim")
                .encode()
                .build()
                .toUri();

        //Spring 요청 제공 클래스
        RequestEntity<Void> req = RequestEntity.get(uri)
                .header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret)
                .build();

        //Spring 제공 RestTemplate
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> resp = restTemplate.exchange(req, String.class); //응답 본문 String 타입으로 변환

        //JSON 파싱 (JSON 문자열을 객체로 만듦, 문서화)
        ObjectMapper om = new ObjectMapper();
        ResultDto resultDto = null;

        try {
            resultDto = om.readValue(resp.getBody(), ResultDto.class);
        } catch (JsonMappingException e) {
            log.error("JsonMappingException occurred while reading value", e);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException occurred while reading value", e);
        }

        //책 정보 데이터 담긴 List 변수 : books
        List<BookDto> books = Optional.ofNullable(resultDto)
                .map(ResultDto::getItems)
                .orElse(Collections.emptyList());

        List<Book> book = books.stream()
                .map(BookDto::toEntity)
                .collect(Collectors.toList());

        //DB 저장
        return bookRepository.saveAll(book);
    }
}
