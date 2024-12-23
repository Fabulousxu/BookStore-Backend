package com.xpg.bookstore.bookstoremain.controller;

import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class BookController {
  @Autowired private BookService bookService;

  @GetMapping("/books")
  public JSONObject searchBooks(String keyword, int pageIndex, int pageSize) {
    return bookService.searchBooks(keyword, pageIndex, pageSize);
  }

  @GetMapping("/book/{id}")
  public JSONObject getBookInfo(@PathVariable long id) {
    return bookService.getBookInfo(id);
  }

  @GetMapping("/books/category")
  public JSONObject searchBooksByCategory(String category, int pageIndex, int pageSize) {
    return bookService.searchBooksByCategory(category, pageIndex, pageSize);
  }

  @QueryMapping
  public JSONObject searchBooksByTitle(
      @Argument String title, @Argument int pageIndex, @Argument int pageSize) {
    return bookService.searchBooksByTitle(title, pageIndex, pageSize);
  }

  @GetMapping("/book/{id}/comments")
  public JSONObject getComments(
      @PathVariable long id,
      int pageIndex,
      int pageSize,
      String sort,
      @SessionAttribute("id") long userId) {
    return bookService.getBookComments(id, pageIndex, pageSize, sort, userId);
  }

  @PostMapping("/book/{id}/comments")
  public JSONObject postComment(
      @PathVariable long id, @RequestBody JSONObject body, @SessionAttribute("id") long userId) {
    return bookService.postComment(id, userId, body.getString("content"));
  }
}
