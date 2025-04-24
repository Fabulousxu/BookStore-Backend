package com.xpg.bookstore.bookstoremain.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.dao.UserDao;
import com.xpg.bookstore.bookstoremain.entity.User;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;

public class UserServiceImplDataFlowTest {

    @Mock
    private UserDao userDao;

    @Mock
    private HttpSession session;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 测试 login 方法的数据流
    @Test
    void login_ShouldFail_WhenUserNotFound() {
        // 定义变量
        String username = "nonexistent";
        String password = "password";

        // 模拟数据流路径
        when(userDao.findByUsername(username)).thenReturn(null);

        // 验证数据流动
        JSONObject result = userService.login(username, password);

        assertEquals("用户不存在", result.getString("message"));
        verify(userDao).findByUsername(username);
    }

    @Test
    void login_ShouldFail_WhenPasswordIncorrect() {
        // 定义变量
        String username = "existing";
        String password = "wrong";
        User user = new User();
        user.setUsername(username);

        // 模拟数据流路径
        when(userDao.findByUsername(username)).thenReturn(user);
        when(userDao.existsByUsernameAndPassword(username, password)).thenReturn(false);

        // 验证数据流动
        JSONObject result = userService.login(username, password);

        assertEquals("密码错误", result.getString("message"));
        verify(userDao).existsByUsernameAndPassword(username, password);
    }

    @Test
    void login_ShouldFail_WhenUserSilenced() {
        // 定义变量
        String username = "silenced";
        String password = "correct";
        User user = new User();
        user.setUsername(username);
        user.setSilence(true);

        // 模拟数据流路径
        when(userDao.findByUsername(username)).thenReturn(user);
        when(userDao.existsByUsernameAndPassword(username, password)).thenReturn(true);

        // 验证数据流动
        JSONObject result = userService.login(username, password);

        assertEquals("您的账户已被禁用", result.getString("message"));
    }

    @Test
    void login_ShouldSuccess_WhenValidCredentials() {
        // 定义变量
        String username = "valid";
        String password = "correct";
        long userId = 1L;
        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        user.setAdmin(true);

        // 模拟数据流路径
        when(userDao.findByUsername(username)).thenReturn(user);
        when(userDao.existsByUsernameAndPassword(username, password)).thenReturn(true);

        // 验证数据流动
        JSONObject result = userService.login(username, password);

        assertEquals("登录成功", result.getString("message"));
        assertTrue(result.getJSONObject("data").getBooleanValue("admin"));
        verify(session).setAttribute("id", userId);
    }

    // 测试 logout 方法的数据流
    @Test
    void logout_ShouldRemoveSessionAttribute() {
        // 定义变量
        long userId = 1L;

        // 验证数据流动
        JSONObject result = userService.logout(userId);

        assertEquals("登出成功", result.getString("message"));
        verify(session).removeAttribute("id");
    }

    // 测试 register 方法的数据流
    @Test
    void register_ShouldFail_WhenUsernameExists() {
        // 定义变量
        String username = "existing";
        String email = "test@example.com";
        String password = "password";

        // 模拟数据流路径
        when(userDao.existsByUsername(username)).thenReturn(true);

        // 验证数据流动
        JSONObject result = userService.register(username, email, password);

        assertEquals("用户已存在", result.getString("message"));
        verify(userDao).existsByUsername(username);
    }

    @Test
    void register_ShouldSuccess_WhenValidInput() {
        // 定义变量
        String username = "newuser";
        String email = "new@example.com";
        String password = "password";

        // 模拟数据流路径
        when(userDao.existsByUsername(username)).thenReturn(false);
        when(userDao.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(1L); // 模拟生成ID
            return user;
        });

        // 验证数据流动
        JSONObject result = userService.register(username, email, password);

        assertEquals("注册成功", result.getString("message"));
        verify(userDao, times(2)).save((User) any()); // 一次User，一次UserAuth
    }

    // 测试 searchUsers 方法的数据流
    @Test
    void searchUsers_ShouldReturnPaginatedResults() {
        // 定义变量
        String keyword = "test";
        int pageIndex = 0;
        int pageSize = 10;

        User user = new User();
        user.setUserId(1L);
        user.setUsername("testuser");

        // 模拟数据流路径
        when(userDao.findByKeyword(keyword, PageRequest.of(pageIndex, pageSize)))
                .thenReturn(new PageImpl<>(Collections.singletonList(user)));

        // 验证数据流动
        JSONObject result = userService.searchUsers(keyword, pageIndex, pageSize);

        assertEquals(1, result.getIntValue("totalNumber"));
        assertEquals(1, result.getJSONArray("items").size());
    }

    // 测试 setUserInfo 方法的数据流
    @Test
    void setUserInfo_ShouldFail_WhenUserNotFound() {
        // 定义变量
        long userId = 999L;
        String username = "new";
        String email = "new@example.com";

        // 模拟数据流路径
        when(userDao.findById(userId)).thenReturn(null);

        // 验证数据流动
        JSONObject result = userService.setUserInfo(userId, username, email);

        assertEquals("用户不存在", result.getString("message"));
        verify(userDao).findById(userId);
    }

    @Test
    void setUserInfo_ShouldSuccess_WhenValidInput() {
        // 定义变量
        long userId = 1L;
        String username = "new";
        String email = "new@example.com";
        User user = new User();
        user.setUserId(userId);

        // 模拟数据流路径
        when(userDao.findById(userId)).thenReturn(user);
        when(userDao.save(user)).thenReturn(user);

        // 验证数据流动
        JSONObject result = userService.setUserInfo(userId, username, email);

        assertEquals("修改成功", result.getString("message"));
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        verify(userDao).save(user);
    }

    // 测试 silenceUser 方法的数据流
    @Test
    void silenceUser_ShouldFail_WhenUserNotFound() {
        // 定义变量
        long userId = 999L;

        // 模拟数据流路径
        when(userDao.findById(userId)).thenReturn(null);

        // 验证数据流动
        JSONObject result = userService.silenceUser(userId);

        assertEquals("用户不存在", result.getString("message"));
    }

    @Test
    void silenceUser_ShouldFail_WhenAlreadySilenced() {
        // 定义变量
        long userId = 1L;
        User user = new User();
        user.setUserId(userId);
        user.setSilence(true);

        // 模拟数据流路径
        when(userDao.findById(userId)).thenReturn(user);

        // 验证数据流动
        JSONObject result = userService.silenceUser(userId);

        assertEquals("用户已被禁用", result.getString("message"));
    }

    @Test
    void silenceUser_ShouldSuccess_WhenValidInput() {
        // 定义变量
        long userId = 1L;
        User user = new User();
        user.setUserId(userId);
        user.setSilence(false);

        // 模拟数据流路径
        when(userDao.findById(userId)).thenReturn(user);
        when(userDao.save(user)).thenReturn(user);

        // 验证数据流动
        JSONObject result = userService.silenceUser(userId);

        assertEquals("禁用成功", result.getString("message"));
        assertTrue(user.getSilence());
        verify(userDao).save(user);
    }

    // 测试 unsilenceUser 方法的数据流
    @Test
    void unsilenceUser_ShouldFail_WhenUserNotFound() {
        // 定义变量
        long userId = 999L;

        // 模拟数据流路径
        when(userDao.findById(userId)).thenReturn(null);

        // 验证数据流动
        JSONObject result = userService.unsilenceUser(userId);

        assertEquals("用户不存在", result.getString("message"));
    }

    @Test
    void unsilenceUser_ShouldFail_WhenNotSilenced() {
        // 定义变量
        long userId = 1L;
        User user = new User();
        user.setUserId(userId);
        user.setSilence(false);

        // 模拟数据流路径
        when(userDao.findById(userId)).thenReturn(user);

        // 验证数据流动
        JSONObject result = userService.unsilenceUser(userId);

        assertEquals("用户未被禁用", result.getString("message"));
    }

    @Test
    void unsilenceUser_ShouldSuccess_WhenValidInput() {
        // 定义变量
        long userId = 1L;
        User user = new User();
        user.setUserId(userId);
        user.setSilence(true);

        // 模拟数据流路径
        when(userDao.findById(userId)).thenReturn(user);
        when(userDao.save(user)).thenReturn(user);

        // 验证数据流动
        JSONObject result = userService.unsilenceUser(userId);

        assertEquals("解禁成功", result.getString("message"));
        assertFalse(user.getSilence());
        verify(userDao).save(user);
    }
}