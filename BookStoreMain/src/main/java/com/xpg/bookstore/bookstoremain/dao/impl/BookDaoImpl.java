package com.xpg.bookstore.bookstoremain.dao.impl;

import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.dao.BookDao;
import com.xpg.bookstore.bookstoremain.entity.Book;
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
  @Autowired private RedisTemplate redisTemplate;

  @Value("${use-redis}")
  private boolean USE_REDIS;

  @Override
  public Book save(Book book) {
    Book savedBook = bookRepository.save(book);
    if (USE_REDIS) {
      try {
        redisTemplate
            .opsForValue()
            .set("book" + savedBook.getBookId(), JSONObject.toJSONString(savedBook));
        System.out.println("[BookDao::save] Book: " + savedBook.getBookId() + " is saved in Redis");
      } catch (Exception e) {
        USE_REDIS = false;
        System.out.println("[BookDao::save] Redis is down");
      }
    }
    return savedBook;
  }

  @Override
  public Book findById(long id) {
    Book book = null;
    String b = null;
    if (USE_REDIS) {
      try {
        b = (String) redisTemplate.opsForValue().get("book" + id);
      } catch (Exception e) {
        USE_REDIS = false;
        System.out.println("[BookDao::findById] Redis is down");
      }
    }
    if (b == null) {
      book = bookRepository.findById(id).orElse(null);
      if (USE_REDIS) {
        try {
          redisTemplate.opsForValue().set("book" + id, JSONObject.toJSONString(book));
        } catch (Exception e) {
          USE_REDIS = false;
          System.out.println("[BookDao::findById] Redis is down");
        }
      }
      System.out.println("[BookDao::findById] Book: " + id + " is in MySQL");
    } else {
      book = JSONObject.parseObject(b, Book.class);
      System.out.println("[BookDao::findById] Book: " + id + " is in Redis");
    }
    return book;
  }

  @Override
  public void deleteById(long id) {
    bookRepository.deleteById(id);
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
  public void delete(Book book) {
    deleteById(book.getBookId());
  }

  @Override
  public Page<Book> findByKeyword(String keyword, Pageable pageable) {
    return bookRepository.findByTitleContainsOrAuthorContainsOrDescriptionContainsOrIsbnContains(
        keyword, keyword, keyword, keyword, pageable);
  }
}
