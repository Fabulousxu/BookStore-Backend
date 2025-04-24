package com.xpg.bookstore.bookstoremain.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.dao.BookDao;
import com.xpg.bookstore.bookstoremain.dao.UserDao;
import com.xpg.bookstore.bookstoremain.entity.Book;
import com.xpg.bookstore.bookstoremain.entity.CartItem;
import com.xpg.bookstore.bookstoremain.entity.User;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplAITest {

  @Mock private BookDao bookDao;

  @Mock private UserDao userDao;

  @InjectMocks private CartServiceImpl cartService;

  // 测试 getCart 方法
  @ParameterizedTest
  @CsvSource({
    "1, true", // 用户存在
    "2, false" // 用户不存在
  })
  void getCart(long userId, boolean userExists) {
    // 模拟数据
    User user = new User();
    user.setUserId(userId);
    List<CartItem> cartItems = new ArrayList<>();
    CartItem item = new CartItem();
    item.setCartItemId(1L);
    Book book = new Book();
    book.setBookId(101L);
    item.setBook(book);
    cartItems.add(item);
    user.setCart(cartItems);

    // 模拟行为
    when(userDao.findById(userId)).thenReturn(userExists ? user : null);

    // 执行测试
    JSONArray result = cartService.getCart(userId);
    System.out.println(result);

    // 验证结果
    if (userExists) {
      assertEquals(1, result.size());
      JSONObject jsonItem = result.getJSONObject(0);
      assertEquals(1L, jsonItem.getLong("id"));
      assertEquals(101L, jsonItem.getJSONObject("book").getLong("id"));
    } else {
      assertTrue(result.isEmpty());
    }

    // 验证方法调用
    verify(userDao, times(1)).findById(userId);
    if (userExists) {
      verify(bookDao, times(1)).loadCover(any(Book.class));
    }
  }

  // 测试 addCart 方法
  @ParameterizedTest
  @CsvSource({
    "101, 1, true, true, false", // 成功加入购物车
    "102, 1, false, true, false", // 书籍不存在
    "103, 2, true, false, false", // 用户不存在
    "104, 1, true, true, true" // 书籍已在购物车中
  })
  void addCart(
      long bookId, long userId, boolean bookExists, boolean userExists, boolean bookInCart) {
    // 模拟数据
    Book book = new Book();
    book.setBookId(bookId);
    User user = new User();
    user.setUserId(userId);
    if (bookInCart) {
      CartItem existingItem = new CartItem();
      existingItem.setBook(book);
      user.setCart(List.of(existingItem));
    } else {
      user.setCart(new ArrayList<>());
    }

    // 模拟行为
    when(bookDao.findById(bookId)).thenReturn(bookExists ? book : null);
    when(userDao.findById(userId)).thenReturn(userExists ? user : null);

    // 执行测试
    JSONObject result = cartService.addCart(bookId, userId);

    // 验证结果
    if (!bookExists) {
      assertEquals("书籍不存在", result.getString("message"));
    } else if (!userExists) {
      assertEquals("用户不存在", result.getString("message"));
    } else if (bookInCart) {
      assertEquals("书籍已在购物车中", result.getString("message"));
    } else {
      assertEquals("成功加入购物车", result.getString("message"));
      assertTrue(user.getCart().stream().anyMatch(item -> item.getBook().getBookId() == bookId));
    }

    // 验证方法调用
    verify(bookDao, times(1)).findById(bookId);
    verify(userDao, times(1)).findById(userId);
    if (bookExists && userExists && !bookInCart) {
      verify(userDao, times(1)).save(user);
    }
  }

  // 测试 setNumber 方法
  @ParameterizedTest
  @CsvSource({
    "1, 1, 5, true, true", // 成功修改数量
    "2, 1, 0, true, true", // 数量不合法
    "3, 2, 5, false, true", // 用户不存在
    "4, 1, 5, true, false" // 购物车项不存在
  })
  void setNumber(
      long cartItemId, long userId, int number, boolean userExists, boolean cartItemExists) {
    // 模拟数据
    User user = new User();
    user.setUserId(userId);
    List<CartItem> cartItems = new ArrayList<>();
    if (cartItemExists) {
      CartItem item = new CartItem();
      item.setCartItemId(cartItemId);
      cartItems.add(item);
    }
    user.setCart(cartItems);

    // 模拟行为
    when(userDao.findById(userId)).thenReturn(userExists ? user : null);

    // 执行测试
    JSONObject result = cartService.setNumber(cartItemId, userId, number);

    // 验证结果
    if (!userExists) {
      assertEquals("用户不存在", result.getString("message"));
    } else if (number <= 0) {
      assertEquals("数量不合法", result.getString("message"));
    } else if (!cartItemExists) {
      assertEquals("购物车项不存在", result.getString("message"));
    } else {
      assertEquals("成功修改数量", result.getString("message"));
      assertEquals(number, user.getCart().get(0).getNumber());
    }

    // 验证方法调用
    verify(userDao, times(1)).findById(userId);
    if (userExists && number > 0 && cartItemExists) {
      verify(userDao, times(1)).save(user);
    }
  }

  // 测试 delCart 方法
  @ParameterizedTest
  @CsvSource({
    "1, 1, true, true", // 成功删除购物车项
    "2, 1, false, true", // 用户不存在
    "3, 1, true, false" // 购物车项不存在
  })
  void delCart(long cartItemId, long userId, boolean userExists, boolean cartItemExists) {
    // 模拟数据
    User user = new User();
    user.setUserId(userId);
    List<CartItem> cartItems = new ArrayList<>();
    if (cartItemExists) {
      CartItem item = new CartItem();
      item.setCartItemId(cartItemId);
      cartItems.add(item);
    }
    user.setCart(cartItems);

    // 模拟行为
    when(userDao.findById(userId)).thenReturn(userExists ? user : null);

    // 执行测试
    JSONObject result = cartService.delCart(cartItemId, userId);

    // 验证结果
    if (!userExists) {
      assertEquals("用户不存在", result.getString("message"));
    } else if (!cartItemExists) {
      assertEquals("购物车项不存在", result.getString("message"));
    } else {
      assertEquals("成功删除购物车项", result.getString("message"));
      assertTrue(user.getCart().isEmpty());
    }

    // 验证方法调用
    verify(userDao, times(1)).findById(userId);
    if (userExists && cartItemExists) {
      verify(userDao, times(1)).save(user);
    }
  }
}
