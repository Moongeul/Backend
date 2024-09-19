package com.core.book.api.bookshelf.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@Table(name = "READBOOKS_TAG")
public class ReadBooksTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "readbooks_tag_id")
    private Long id;

    private String tag1;
    private String tag2;
    private String tag3;
    private String tag4;
    private String tag5;
}
