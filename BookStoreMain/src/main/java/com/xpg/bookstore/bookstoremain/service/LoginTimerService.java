package com.xpg.bookstore.bookstoremain.service;

import org.springframework.stereotype.Service;

@Service
public interface LoginTimerService {
  void login();
  long logout();
}