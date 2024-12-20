package com.xpg.bookstore.bookstoremain.dao.impl;

import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.dao.BookDao;
import com.xpg.bookstore.bookstoremain.entity.Book;
import com.xpg.bookstore.bookstoremain.entity.BookCover;
import com.xpg.bookstore.bookstoremain.repository.BookCoverRepository;
import com.xpg.bookstore.bookstoremain.repository.BookRepository;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BookDaoImpl implements BookDao {
  @Autowired private BookRepository bookRepository;
  @Autowired private BookCoverRepository bookCoverRepository;
  @Autowired private RedisTemplate redisTemplate;

  @Value("${use-redis}")
  private boolean USE_REDIS;

  @Override
  public Book save(Book book) {
    book = bookRepository.save(book);
    bookCoverRepository.save(new BookCover(book.getBookId(), book.getCover()));
    if (USE_REDIS) {
      try {
        redisTemplate.opsForValue().set("book" + book.getBookId(), JSONObject.toJSONString(book));
        System.out.println("[BookDao::save] Book: " + book.getBookId() + " is saved in Redis");
      } catch (Exception e) {
        USE_REDIS = false;
        System.out.println("[BookDao::save] Redis is down");
      }
    }
    return book;
  }

  @Override
  public Book findById(long id) {
    String redisBook = null;
    if (USE_REDIS) {
      try {
        redisBook = (String) redisTemplate.opsForValue().get("book" + id);
      } catch (Exception e) {
        USE_REDIS = false;
        System.out.println("[BookDao::findById] Redis is down");
      }
    }
    if (redisBook != null) {
      System.out.println("[BookDao::findById] Book: " + id + " is in Redis");
      return JSONObject.parseObject(redisBook, Book.class);
    }
    Book book = bookRepository.findById(id).orElse(null);
    if (book == null) return null;
    loadCover(book);
    if (USE_REDIS) {
      try {
        redisTemplate.opsForValue().set("book" + id, JSONObject.toJSONString(book));
      } catch (Exception e) {
        USE_REDIS = false;
        System.out.println("[BookDao::findById] Redis is down");
      }
    }
    System.out.println("[BookDao::findById] Book: " + id + " is in MySQL");
    return book;
  }

  @Override
  public boolean existsById(long id) {
    if (USE_REDIS) {
      try {
        if (redisTemplate.hasKey("book" + id)) {
          System.out.println("[BookDao::existsById] Book: " + id + " is in Redis");
          return true;
        }
      } catch (Exception e) {
        USE_REDIS = false;
        System.out.println("[BookDao::existsById] Redis is down");
      }
    }
    if (bookRepository.existsById(id)) {
      System.out.println("[BookDao::existsById] Book: " + id + " is in MySQL");
      return true;
    } else return false;
  }

  @Override
  public void deleteById(long id) {
    bookRepository.deleteById(id);
    bookCoverRepository.deleteById(String.valueOf(id));
    if (USE_REDIS) {
      try {
        if (redisTemplate.delete("book" + id))
          System.out.println("[BookDao::deleteById] Book: " + id + " is deleted from Redis");
        else System.out.println("[BookDao::deleteById] Book: " + id + " is not in Redis");
      } catch (Exception e) {
        USE_REDIS = false;
        System.out.println("[BookDao::deleteById] Redis is down");
      }
    }
  }

  @Override
  public Page<Book> findByKeyword(String keyword, Pageable pageable) {
    Page<Book> bookPage =
        bookRepository.findByTitleContainsOrAuthorContainsOrDescriptionContainsOrIsbnContains(
            keyword, keyword, keyword, keyword, pageable);
    for (Book book : bookPage) loadCover(book);
    return bookPage;
  }

  @Override
  public Page<Book> findByCategoryCodeContains(Set<String> categoryCodes, Pageable pageable) {
    List<String> categoryCodesList = categoryCodes.stream().toList();
    Page<Book> bookPage =
        bookRepository
            .findByCategoryCodeContainsOrCategoryCodeContainsOrCategoryCodeContainsOrCategoryCodeContainsOrCategoryCodeContainsOrCategoryCodeContainsOrCategoryCodeContainsOrCategoryCodeContains(
                !categoryCodesList.isEmpty() ? categoryCodesList.get(0) : "",
                categoryCodesList.size() > 1 ? categoryCodesList.get(1) : "",
                categoryCodesList.size() > 2 ? categoryCodesList.get(2) : "",
                categoryCodesList.size() > 3 ? categoryCodesList.get(3) : "",
                categoryCodesList.size() > 4 ? categoryCodesList.get(4) : "",
                categoryCodesList.size() > 5 ? categoryCodesList.get(5) : "",
                categoryCodesList.size() > 6 ? categoryCodesList.get(6) : "",
                categoryCodesList.size() > 7 ? categoryCodesList.get(7) : "",
                pageable);
    for (Book book : bookPage) loadCover(book);
    return bookPage;
  }

  @Override
  public Page<Book> findByTitleContains(String title, Pageable pageable) {
    Page<Book> bookPage = bookRepository.findByTitleContains(title, pageable);
    for (Book book : bookPage) loadCover(book);
    return bookPage;
  }

  @Override
  public void loadCover(Book book) {
    bookCoverRepository
        .findById(String.valueOf(book.getBookId()))
        .ifPresent(bookCover -> book.setCover(bookCover.getCover()));
  }
}
