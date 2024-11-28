package com.xpg.bookstore.bookstoremain.dao.impl;

import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.dao.BookDao;
import com.xpg.bookstore.bookstoremain.entity.Book;
import com.xpg.bookstore.bookstoremain.entity.BookCover;
import com.xpg.bookstore.bookstoremain.repository.BookCoverRepository;
import com.xpg.bookstore.bookstoremain.repository.BookRepository;
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
  public void loadCover(Book book) {
    bookCoverRepository
        .findById(String.valueOf(book.getBookId()))
        .ifPresent(bookCover -> book.setCover(bookCover.getCover()));
  }
}
