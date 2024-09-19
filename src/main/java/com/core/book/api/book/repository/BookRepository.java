package com.core.book.api.book.repository;

import com.core.book.api.book.entity.Book;
import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<Book, String> {

    boolean existsByIsbn(String isbn);
}
