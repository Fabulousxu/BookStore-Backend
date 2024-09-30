package com.example.bookstore.service.impl;

import com.example.bookstore.service.LoginTimerService;
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
