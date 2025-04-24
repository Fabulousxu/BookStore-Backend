package com.xpg.bookstore.bookstoremain.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LoginTimerServiceImplDUPathTest {
  @InjectMocks private LoginTimerServiceImpl loginTimerService;

  @Test
  void login() {
    loginTimerService.login();
  }

  @Test
  void logout() throws InterruptedException {
    loginTimerService.login();
    Thread.sleep(1000);
    long duration = loginTimerService.logout();
    assertTrue(duration > 1000);
  }
}
