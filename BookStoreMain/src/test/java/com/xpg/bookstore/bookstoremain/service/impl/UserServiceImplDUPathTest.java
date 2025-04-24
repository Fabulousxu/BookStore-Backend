package com.xpg.bookstore.bookstoremain.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.dao.UserDao;
import com.xpg.bookstore.bookstoremain.entity.User;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplDUPathTest {
  @InjectMocks private UserServiceImpl userService;
  @Mock private UserDao userDao;
  @Mock private HttpSession session;

  @Test
  void login() {
    User user1 = new User();
    user1.setUserId(1L);
    user1.setSilence(true);
    User user2 = new User();
    user2.setUserId(2L);
    user2.setSilence(false);
    when(userDao.findByUsername(eq("u1"))).thenReturn(user1);
    when(userDao.findByUsername(eq("u2"))).thenReturn(user2);
    when(userDao.findByUsername(eq("u3"))).thenReturn(null);
    when(userDao.existsByUsernameAndPassword(any(String.class), eq("right"))).thenReturn(true);
    when(userDao.existsByUsernameAndPassword(any(String.class), eq("false"))).thenReturn(false);

    JSONObject noExistUserResult = userService.login("u3", "right");
    assertFalse(noExistUserResult.getBoolean("ok"));
    JSONObject passwordErrorResult = userService.login("u1", "false");
    assertFalse(passwordErrorResult.getBoolean("ok"));
    JSONObject silenceUserResult = userService.login("u1", "right");
    assertFalse(silenceUserResult.getBoolean("ok"));
    JSONObject normalUserResult = userService.login("u2", "right");
    assertTrue(normalUserResult.getBoolean("ok"));
  }

  @Test
  void logout() {
    JSONObject result = userService.logout(1L);
    assertTrue(result.getBoolean("ok"));
  }

  @Test
  void register() {
    when(userDao.existsByUsername(eq("u1"))).thenReturn(true);
    when(userDao.existsByUsername(eq("u2"))).thenReturn(false);

    JSONObject existedUserResult = userService.register("u1", "email", "password");
    assertFalse(existedUserResult.getBoolean("ok"));
    JSONObject normalUserResult = userService.register("u2", "email", "password");
    assertTrue(normalUserResult.getBoolean("ok"));
  }

  @Test
  void searchUsers() {
    User user1 = new User();
    user1.setUsername("u1");
    User user2 = new User();
    user2.setUserId(2L);
    user2.setUsername("u2");
    List<User> user1List = List.of(user1);
    Page<User> user1Page = new PageImpl<>(user1List, PageRequest.of(0, 10), 1);
    when(userDao.findByKeyword(eq("u1"), any())).thenReturn(user1Page);
    List<User> allUserList = List.of(user1, user2);
    Page<User> allUserPage = new PageImpl<>(allUserList, PageRequest.of(0, 10), 2);
    when(userDao.findByKeyword(eq("u"), any())).thenReturn(allUserPage);
    List<User> noneUserList = List.of();
    Page<User> noneUserPage = new PageImpl<>(noneUserList, PageRequest.of(0, 10), 0);
    when(userDao.findByKeyword(eq("none"), any())).thenReturn(noneUserPage);

    JSONObject user1Result = userService.searchUsers("u1", 0, 10);
    assertEquals(1, user1Result.getInteger("totalNumber"));
    assertEquals(1, user1Result.getInteger("totalPage"));
    JSONObject allUserResult = userService.searchUsers("u", 0, 10);
    assertEquals(2, allUserResult.getInteger("totalNumber"));
    assertEquals(1, allUserResult.getInteger("totalPage"));
    JSONObject noneUserResult = userService.searchUsers("none", 0, 10);
    assertEquals(0, noneUserResult.getInteger("totalNumber"));
    assertEquals(0, noneUserResult.getInteger("totalPage"));
  }

  @Test
  void setUserInfo() {
    User user = new User();
    when(userDao.findById(eq(1L))).thenReturn(user);
    when(userDao.findById(eq(2L))).thenReturn(null);
    JSONObject successResult = userService.setUserInfo(1L, "u1", "email");
    assertTrue(successResult.getBoolean("ok"));
    JSONObject failedResult = userService.setUserInfo(2L, "u1", "email");
    assertFalse(failedResult.getBoolean("ok"));
  }

  @Test
  void silenceUser() {
    User user1 = new User();
    user1.setSilence(true);
    User user2 = new User();
    user2.setSilence(false);
    when(userDao.findById(eq(1L))).thenReturn(user1);
    when(userDao.findById(eq(2L))).thenReturn(user2);
    when(userDao.findById(eq(3L))).thenReturn(null);

    JSONObject noExistUserResult = userService.silenceUser(3L);
    assertFalse(noExistUserResult.getBoolean("ok"));
    JSONObject alreadySilenceUserResult = userService.silenceUser(1L);
    assertFalse(alreadySilenceUserResult.getBoolean("ok"));
    JSONObject normalUserResult = userService.silenceUser(2L);
    assertTrue(normalUserResult.getBoolean("ok"));
  }

  @Test
  void unsilenceUser() {
    User user1 = new User();
    user1.setSilence(true);
    User user2 = new User();
    user2.setSilence(false);
    when(userDao.findById(eq(1L))).thenReturn(user1);
    when(userDao.findById(eq(2L))).thenReturn(user2);
    when(userDao.findById(eq(3L))).thenReturn(null);

    JSONObject noExistUserResult = userService.unsilenceUser(3L);
    assertFalse(noExistUserResult.getBoolean("ok"));
    JSONObject alreadyUnsilenceUserResult = userService.unsilenceUser(2L);
    assertFalse(alreadyUnsilenceUserResult.getBoolean("ok"));
    JSONObject normalUserResult = userService.unsilenceUser(1L);
    assertTrue(normalUserResult.getBoolean("ok"));
  }
}
