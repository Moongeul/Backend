package com.core.book.api.book.dto;

import lombok.*;
import java.util.List;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ResultDTO {
    private String lastBuildDate;
    private int total;
    private int start;
    private int display;
    private List<BookDTO> items;
}