package com.xpg.bookstore.bookstoremain.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.dao.UserDao;
import com.xpg.bookstore.bookstoremain.entity.User;
import com.xpg.bookstore.bookstoremain.entity.UserAuth;
import com.xpg.bookstore.bookstoremain.util.Util;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplAITest {

  @Mock private UserDao userDao;

  @Mock private HttpSession session;

  @InjectMocks private UserServiceImpl userService;

  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setUserId(1L);
    user.setUsername("testUser");
    user.setEmail("test@example.com");
    user.setSilence(false);
    user.setAdmin(false);

    UserAuth userAuth = new UserAuth();
    userAuth.setUserId(1L);
    userAuth.setPassword("password");
  }

  @ParameterizedTest
  @MethodSource("loginProvider")
  void testLogin(
      String username,
      String password,
      User user,
      boolean existsByUsernameAndPassword,
      boolean silence,
      JSONObject expected) {
    // Mock userDao 方法
    when(userDao.findByUsername(username)).thenReturn(user);
    lenient()
        .when(userDao.existsByUsernameAndPassword(username, password))
        .thenReturn(existsByUsernameAndPassword);

    // 只有在 user 不为 null 时才 mock getSilence()
    if (user != null) {
      when(user.getSilence()).thenReturn(silence);
    }

    // 调用测试方法
    JSONObject result = userService.login(username, password);

    // 验证结果
    assertEquals(expected.toJSONString(), result.toJSONString());

    // 如果需要，验证 session 的设置
    if (expected.getBoolean("ok")) {
      verify(session).setAttribute("id", user.getUserId());
    }
  }

  private static Stream<Arguments> loginProvider() {
    User user = mock(User.class); // 使用 mock 对象
    when(user.getUserId()).thenReturn(1L);
    when(user.getUsername()).thenReturn("testUser");
    when(user.getSilence()).thenReturn(false);
    when(user.getAdmin()).thenReturn(false);

    return Stream.of(
        // 测试登录成功
        Arguments.of(
            "testUser",
            "password",
            user,
            true,
            false,
            Util.successResponseJson("登录成功")
                .fluentPut("data", new JSONObject().fluentPut("admin", false))),
        // 测试用户不存在
        Arguments.of(
            "nonExistentUser", "password", null, false, false, Util.errorResponseJson("用户不存在")),
        // 测试密码错误
        Arguments.of(
            "testUser", "wrongPassword", user, false, false, Util.errorResponseJson("密码错误")),
        // 测试账户被禁用
        Arguments.of("testUser", "password", user, true, true, Util.errorResponseJson("您的账户已被禁用")));
  }

  @Test
  void testLogout() {
    JSONObject result = userService.logout(1L);

    assertEquals(Util.successResponseJson("登出成功"), result);
    verify(session).removeAttribute("id");
  }

  @ParameterizedTest
  @MethodSource("registerProvider")
  void testRegister(
      String username,
      String email,
      String password,
      boolean existsByUsername,
      JSONObject expected) {
    when(userDao.existsByUsername(username)).thenReturn(existsByUsername);

    JSONObject result = userService.register(username, email, password);

    assertEquals(expected, result);
    if (!existsByUsername) {
      verify(userDao, times(1)).save((User) any());
    }
  }

  private static Stream<Arguments> registerProvider() {
    return Stream.of(
        Arguments.of(
            "newUser", "new@example.com", "password", false, Util.successResponseJson("注册成功")),
        Arguments.of(
            "existingUser",
            "existing@example.com",
            "password",
            true,
            Util.errorResponseJson("用户已存在")));
  }

  @Test
  void testSearchUsers() {
    Page<User> userPage = mock(Page.class);
    when(userDao.findByKeyword("keyword", PageRequest.of(0, 10))).thenReturn(userPage);
    when(userPage.getTotalElements()).thenReturn(1L);
    when(userPage.getTotalPages()).thenReturn(1);
    when(userPage.iterator()).thenReturn(Stream.of(user).iterator());

    JSONObject result = userService.searchUsers("keyword", 0, 10);

    assertEquals(1L, result.getLong("totalNumber"));
    assertEquals(1, result.getInteger("totalPage"));
    assertTrue(result.getJSONArray("items").contains(user));
  }

  @ParameterizedTest
  @MethodSource("setUserInfoProvider")
  void testSetUserInfo(long userId, String username, String email, User user, JSONObject expected) {
    when(userDao.findById(userId)).thenReturn(user);

    JSONObject result = userService.setUserInfo(userId, username, email);

    assertEquals(expected, result);
    if (user != null) {
      verify(userDao).save(user);
    }
  }

  private static Stream<Arguments> setUserInfoProvider() {
    User user = new User();
    user.setUserId(1L);
    user.setUsername("oldUsername");
    user.setEmail("old@example.com");

    return Stream.of(
        Arguments.of(1L, "newUsername", "new@example.com", user, Util.successResponseJson("修改成功")),
        Arguments.of(2L, "newUsername", "new@example.com", null, Util.errorResponseJson("用户不存在")));
  }

  @ParameterizedTest
  @MethodSource("silenceUserProvider")
  void testSilenceUser(long userId, User user, boolean silence, JSONObject expected) {
    when(userDao.findById(userId)).thenReturn(user);

    JSONObject result = userService.silenceUser(userId);

    assertEquals(expected, result);
    if (user != null && !silence) {
      verify(userDao).save(user);
    }
  }

  private static Stream<Arguments> silenceUserProvider() {
    User user = new User();
    user.setUserId(1L);
    user.setSilence(false);

    return Stream.of(
        Arguments.of(1L, user, false, Util.successResponseJson("禁用成功")),
        Arguments.of(2L, null, false, Util.errorResponseJson("用户不存在")),
        Arguments.of(1L, user, true, Util.errorResponseJson("用户已被禁用")));
  }

  @ParameterizedTest
  @MethodSource("unsilenceUserProvider")
  void testUnsilenceUser(long userId, User user, boolean silence, JSONObject expected) {
    when(userDao.findById(userId)).thenReturn(user);

    JSONObject result = userService.unsilenceUser(userId);

    assertEquals(expected, result);
    if (user != null && silence) {
      verify(userDao).save(user);
    }
  }

  private static Stream<Arguments> unsilenceUserProvider() {
    User user = new User();
    user.setUserId(1L);
    user.setSilence(true);

    return Stream.of(
        Arguments.of(1L, user, true, Util.successResponseJson("解禁成功")),
        Arguments.of(2L, null, false, Util.errorResponseJson("用户不存在")),
        Arguments.of(1L, user, false, Util.errorResponseJson("用户未被禁用")));
  }
}
