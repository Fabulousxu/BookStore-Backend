package com.example.bookstore.dao;

import com.example.bookstore.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface BookDao {
  Book save(Book book);

  Book findById(long id);

  void deleteById(long id);

  void delete(Book book);

  Page<Book> findByKeyword(String keyword, Pageable pageable);
}
