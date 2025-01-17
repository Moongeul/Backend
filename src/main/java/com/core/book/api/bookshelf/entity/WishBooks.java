package com.core.book.api.bookshelf.entity;

import com.core.book.api.book.entity.Book;
import com.core.book.api.member.entity.Member;
import com.core.book.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@Table(name = "WISHBOOKS")
public class WishBooks extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishbooks_id")
    private Long id;
    
    private String reason; //읽고 싶은 이유

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book; //책

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member; //회원
}
