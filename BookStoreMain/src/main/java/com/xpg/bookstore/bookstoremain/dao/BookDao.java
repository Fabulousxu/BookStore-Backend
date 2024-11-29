package com.xpg.bookstore.bookstoremain.dao;

import com.xpg.bookstore.bookstoremain.entity.Book;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface BookDao {
  Book save(Book book);

  Book findById(long id);

  boolean existsById(long id);

  void deleteById(long id);

  Page<Book> findByKeyword(String keyword, Pageable pageable);

  Page<Book> findByCategoryCodeContains(Set<String> categoryCodes, Pageable pageable);

  void loadCover(Book book);
}
