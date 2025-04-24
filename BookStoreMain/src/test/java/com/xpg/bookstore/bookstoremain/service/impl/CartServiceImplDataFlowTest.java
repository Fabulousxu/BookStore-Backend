package com.xpg.bookstore.bookstoremain.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.dao.BookDao;
import com.xpg.bookstore.bookstoremain.dao.UserDao;
import com.xpg.bookstore.bookstoremain.entity.Book;
import com.xpg.bookstore.bookstoremain.entity.CartItem;
import com.xpg.bookstore.bookstoremain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

public class CartServiceImplDataFlowTest {

  @Mock private BookDao bookDao;

  @Mock private UserDao userDao;

  @InjectMocks private CartServiceImpl cartService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  // 测试getCart方法的数据流
  @Test
  void getCart_ShouldReturnEmpty_WhenUserNotFound() {
    // 定义变量
    long nonExistingUserId = 999L;

    // 模拟数据流
    when(userDao.findById(nonExistingUserId)).thenReturn(null);

    // 验证数据流动
    JSONArray result = cartService.getCart(nonExistingUserId);

    assertTrue(result.isEmpty());
    verify(userDao).findById(nonExistingUserId);
  }

  @Test
  void getCart_ShouldReturnItems_WhenUserHasCart() {
    // 定义变量和数据流路径
    long userId = 1L;
    User user = new User();
    user.setUserId(userId);

    Book book1 = new Book();
    book1.setBookId(101L);
    Book book2 = new Book();
    book2.setBookId(102L);

    CartItem item1 = new CartItem();
    item1.setCartItemId(1L);
    item1.setBook(book1);
    item1.setUser(user);

    CartItem item2 = new CartItem();
    item2.setCartItemId(2L);
    item2.setBook(book2);
    item2.setUser(user);

    user.setCart(List.of(item1, item2));

    // 模拟数据流
    when(userDao.findById(userId)).thenReturn(user);
    doAnswer(
            invocation -> {
              Book book = invocation.getArgument(0);
              book.setCover("cover_path.jpg");
              return null;
            })
        .when(bookDao)
        .loadCover(any(Book.class));

    // 验证数据流动
    JSONArray result = cartService.getCart(userId);

    assertEquals(2, result.size());
    verify(userDao).findById(userId);
    verify(bookDao, times(2)).loadCover(any(Book.class));
  }

  // 测试addCart方法的数据流
  @Test
  void addCart_ShouldFail_WhenBookNotFound() {
    // 定义变量
    long nonExistingBookId = 999L;
    long userId = 1L;

    // 模拟数据流路径
    when(bookDao.findById(nonExistingBookId)).thenReturn(null);
    when(userDao.findById(userId)).thenReturn(new User());

    // 验证数据流动
    JSONObject result = cartService.addCart(nonExistingBookId, userId);

    assertEquals("书籍不存在", result.getString("message"));
    verify(bookDao).findById(nonExistingBookId);
  }

  @Test
  void addCart_ShouldFail_WhenUserNotFound() {
    // 定义变量
    long bookId = 1L;
    long nonExistingUserId = 999L;
    Book book = new Book();

    // 模拟数据流路径
    when(bookDao.findById(bookId)).thenReturn(book);
    when(userDao.findById(nonExistingUserId)).thenReturn(null);

    // 验证数据流动
    JSONObject result = cartService.addCart(bookId, nonExistingUserId);

    assertEquals("用户不存在", result.getString("message"));
    verify(userDao).findById(nonExistingUserId);
  }

  @Test
  void addCart_ShouldFail_WhenBookAlreadyInCart() {
    // 定义变量和数据流路径
    long bookId = 1L;
    long userId = 1L;

    Book book = new Book();
    book.setBookId(bookId);

    User user = new User();
    user.setUserId(userId);

    CartItem existingItem = new CartItem();
    existingItem.setBook(book);
    existingItem.setUser(user);
    user.setCart(List.of(existingItem));

    // 模拟数据流
    when(bookDao.findById(bookId)).thenReturn(book);
    when(userDao.findById(userId)).thenReturn(user);

    // 验证数据流动
    JSONObject result = cartService.addCart(bookId, userId);

    assertEquals("书籍已在购物车中", result.getString("message"));
    verify(userDao).findById(userId);
  }

  @Test
  void addCart_ShouldSuccess_WhenValidData() {
    // 定义变量和数据流路径
    long bookId = 1L;
    long userId = 1L;

    Book book = new Book();
    book.setBookId(bookId);

    User user = new User();
    user.setUserId(userId);
    user.setCart(new ArrayList<>());

    // 模拟数据流
    when(bookDao.findById(bookId)).thenReturn(book);
    when(userDao.findById(userId)).thenReturn(user);
    when(userDao.save(user)).thenReturn(user);

    // 验证数据流动
    JSONObject result = cartService.addCart(bookId, userId);

    assertEquals("成功加入购物车", result.getString("message"));
    assertEquals(1, user.getCart().size());
    verify(userDao).save(user);
  }

  // 测试setNumber方法的数据流
  @Test
  void setNumber_ShouldFail_WhenUserNotFound() {
    // 定义变量
    long cartItemId = 1L;
    long nonExistingUserId = 999L;
    int number = 2;

    // 模拟数据流路径
    when(userDao.findById(nonExistingUserId)).thenReturn(null);

    // 验证数据流动
    JSONObject result = cartService.setNumber(cartItemId, nonExistingUserId, number);

    assertEquals("用户不存在", result.getString("message"));
    verify(userDao).findById(nonExistingUserId);
  }

  @Test
  void setNumber_ShouldFail_WhenInvalidNumber() {
    // 定义变量
    long cartItemId = 1L;
    long userId = 1L;
    int invalidNumber = 0;

    User user = new User();
    user.setUserId(userId);
    user.setCart(new ArrayList<>());

    // 模拟数据流路径
    when(userDao.findById(userId)).thenReturn(user);

    // 验证数据流动
    JSONObject result = cartService.setNumber(cartItemId, userId, invalidNumber);

    assertEquals("数量不合法", result.getString("message"));
    verify(userDao).findById(userId);
  }

  @Test
  void setNumber_ShouldFail_WhenCartItemNotFound() {
    // 定义变量
    long nonExistingCartItemId = 999L;
    long userId = 1L;
    int number = 2;

    User user = new User();
    user.setUserId(userId);
    user.setCart(new ArrayList<>());

    // 模拟数据流路径
    when(userDao.findById(userId)).thenReturn(user);

    // 验证数据流动
    JSONObject result = cartService.setNumber(nonExistingCartItemId, userId, number);

    assertEquals("购物车项不存在", result.getString("message"));
    verify(userDao).findById(userId);
  }

  @Test
  void setNumber_ShouldSuccess_WhenValidData() {
    // 定义变量和数据流路径
    long cartItemId = 1L;
    long userId = 1L;
    int newNumber = 3;

    Book book = new Book();
    book.setBookId(101L);

    User user = new User();
    user.setUserId(userId);

    CartItem item = new CartItem();
    item.setCartItemId(cartItemId);
    item.setBook(book);
    item.setUser(user);
    item.setNumber(1);

    user.setCart(List.of(item));

    // 模拟数据流
    when(userDao.findById(userId)).thenReturn(user);
    when(userDao.save(user)).thenReturn(user);

    // 验证数据流动
    JSONObject result = cartService.setNumber(cartItemId, userId, newNumber);

    assertEquals("成功修改数量", result.getString("message"));
    assertEquals(newNumber, item.getNumber());
    verify(userDao).save(user);
  }

  // 测试delCart方法的数据流
  @Test
  void delCart_ShouldFail_WhenUserNotFound() {
    // 定义变量
    long cartItemId = 1L;
    long nonExistingUserId = 999L;

    // 模拟数据流路径
    when(userDao.findById(nonExistingUserId)).thenReturn(null);

    // 验证数据流动
    JSONObject result = cartService.delCart(cartItemId, nonExistingUserId);

    assertEquals("用户不存在", result.getString("message"));
    verify(userDao).findById(nonExistingUserId);
  }

  @Test
  void delCart_ShouldFail_WhenCartItemNotFound() {
    // 定义变量
    long nonExistingCartItemId = 999L;
    long userId = 1L;

    User user = new User();
    user.setUserId(userId);
    user.setCart(new ArrayList<>());

    // 模拟数据流路径
    when(userDao.findById(userId)).thenReturn(user);

    // 验证数据流动
    JSONObject result = cartService.delCart(nonExistingCartItemId, userId);

    assertEquals("购物车项不存在", result.getString("message"));
    verify(userDao).findById(userId);
  }

  @Test
  void delCart_ShouldSuccess_WhenValidData() {
    // 定义变量和数据流路径
    long cartItemId = 1L;
    long userId = 1L;

    Book book = new Book();
    book.setBookId(101L);

    User user = new User();
    user.setUserId(userId);

    CartItem item = new CartItem();
    item.setCartItemId(cartItemId);
    item.setBook(book);
    item.setUser(user);

    user.setCart(new ArrayList<>(List.of(item)));

    // 模拟数据流
    when(userDao.findById(userId)).thenReturn(user);
    when(userDao.save(user)).thenReturn(user);

    // 验证数据流动
    JSONObject result = cartService.delCart(cartItemId, userId);

    assertEquals("成功删除购物车项", result.getString("message"));
    assertTrue(user.getCart().isEmpty());
    verify(userDao).save(user);
  }
}
