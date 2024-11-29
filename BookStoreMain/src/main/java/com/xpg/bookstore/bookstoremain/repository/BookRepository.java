package com.xpg.bookstore.bookstoremain.repository;

import com.xpg.bookstore.bookstoremain.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
  Page<Book> findByTitleContainsOrAuthorContainsOrDescriptionContainsOrIsbnContains(
      String title, String author, String description, String isbn, Pageable pageable);

  Page<Book>
      findByCategoryCodeContainsOrCategoryCodeContainsOrCategoryCodeContainsOrCategoryCodeContainsOrCategoryCodeContainsOrCategoryCodeContainsOrCategoryCodeContainsOrCategoryCodeContains(
          String category1,
          String category2,
          String category3,
          String category4,
          String category5,
          String category6,
          String category7,
          String category8,
          Pageable pageable);
}
