package com.core.book.api.bookshelf.entity;

import com.core.book.api.book.entity.Book;
import com.core.book.api.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "READBOOKS")
public class ReadBooks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "readbooks_id")
    private Long id;

    private LocalDate read_date; //읽은 날짜
    private double star_rating; //평점
    private String one_line_review; //한줄평

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "book_id")
    private Book book; //책

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private Member member; //회원

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "readbooks_tag_id")
    private ReadBooksTag readBooksTag; //태그
}
