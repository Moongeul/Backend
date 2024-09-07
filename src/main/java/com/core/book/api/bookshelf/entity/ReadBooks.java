package com.core.book.api.bookshelf.entity;

import com.core.book.api.book.entity.Book;
import com.core.book.api.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@Table(name = "READBOOKS")
public class ReadBooks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "readbooks_id")
    private Long id;

    @Column(name = "read_date")
    private LocalDate readDate; //읽은 날짜
    private double star_rating; //평점
    private String one_line_review; //한줄평

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book; //책

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member; //회원

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "readbooks_tag_id")
    private ReadBooksTag readBooksTag; //태그
}
