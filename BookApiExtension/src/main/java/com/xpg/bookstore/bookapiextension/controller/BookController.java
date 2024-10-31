package com.xpg.bookstore.bookapiextension.controller;

import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookapiextension.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/book")
public class BookController {
  @Autowired private BookService bookService;

  @GetMapping("/{title}/author")
  JSONObject getAuthor(@PathVariable String title) {
    return bookService.getAuthor(title);
  }
}
