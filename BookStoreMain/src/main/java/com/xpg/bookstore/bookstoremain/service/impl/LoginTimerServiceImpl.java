package com.xpg.bookstore.bookstoremain.service.impl;

import com.xpg.bookstore.bookstoremain.service.LoginTimerService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("session")
public class LoginTimerServiceImpl implements LoginTimerService {
  private long loginTime;

  public void login() {
    this.loginTime = System.currentTimeMillis();
  }

  public long logout() {
    return System.currentTimeMillis() - this.loginTime;
  }

}
