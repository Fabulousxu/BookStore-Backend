package com.xpg.bookstore.bookstoremain.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class LoginTimerServiceImplDataFlowTest {
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
