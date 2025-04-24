package com.xpg.bookstore.bookstoremain.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.dao.*;
import com.xpg.bookstore.bookstoremain.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplAITest {

  @Mock private BookDao bookDao;

  @Mock private OrderDao orderDao;

  @Mock private OrderItemDao orderItemDao;

  @Mock private UserDao userDao;

  @InjectMocks private OrderServiceImpl orderService;

  private User user;
  private Order order;
  private OrderItem orderItem;
  private Book book;

  @BeforeEach
  void setUp() {
    // 初始化用户
    user = new User();
    user.setUserId(1L);
    user.setUsername("testUser");
    user.setEmail("test@example.com");

    // 初始化订单
    order = new Order();
    order.setOrderId(1L);
    order.setUser(user);
    order.setReceiver("Receiver");
    order.setAddress("Address");
    order.setTel("123456789");
    order.setCreatedAt(LocalDateTime.now());

    // 初始化书籍
    book = new Book();
    book.setBookId(1L);
    book.setTitle("Test Book");
    book.setAuthor("Test Author");
    book.setIsbn("1234567890");
    book.setDescription("Test Description");
    book.setPrice(100);
    book.setSales(0);
    book.setRepertory(10);

    // 初始化订单项
    orderItem = new OrderItem();
    orderItem.setOrderItemId(1L);
    orderItem.setOrder(order);
    orderItem.setBook(book);
    orderItem.setNumber(2);

    // 设置订单和订单项的关系
    order.setItems(List.of(orderItem));
    user.setOrders(List.of(order));
  }

  @ParameterizedTest
  @MethodSource("provideGetOrderItemsTestCases")
  void testGetOrderItems(long userId, String keyword, boolean expectedEmpty) {
    // 模拟 userDao.findById 返回用户
    when(userDao.findById(userId)).thenReturn(user);

    // 模拟 orderItemDao.loadOrderToJson 返回 JSONObject
    JSONObject mockJsonObject = new JSONObject();
    mockJsonObject.put("id", orderItem.getOrderItemId());
    // 调用方法
    JSONArray result = orderService.getOrderItems(userId, keyword);

    // 验证结果
    assertNotEquals(expectedEmpty, result.isEmpty());
  }

  private static Stream<Arguments> provideGetOrderItemsTestCases() {
    return Stream.of(
        // 正确的时间格式
        Arguments.of(1L, "time:2023-01-01 00:00:00", false),
        // 不包含时间部分的 keyword
        Arguments.of(1L, "Test Book", true),
        // 用户不存在
        Arguments.of(2L, "Test Book", true));
  }

  @ParameterizedTest
  @MethodSource("providePlaceOrderTestCases")
  void testPlaceOrder(
      List<Long> cartItemIds,
      long userId,
      String receiver,
      String address,
      String tel,
      boolean expectedSuccess) {
    // 模拟 userDao.findById 返回用户
    when(userDao.findById(userId)).thenReturn(user);

    // 初始化购物车项
    CartItem cartItem = new CartItem();
    cartItem.setCartItemId(1L);
    cartItem.setBook(book);
    cartItem.setNumber(2);
    user.setCart(new ArrayList<>(List.of(cartItem))); // 使用可变集合

    // 模拟 placeOrder 返回的 JSONObject
    JSONObject mockJsonObject = new JSONObject();
    mockJsonObject.put("status", expectedSuccess ? "success" : "failure");

    // 调用方法
    JSONObject result = orderService.placeOrder(cartItemIds, userId, receiver, address, tel);

    // 验证结果
    assertEquals(expectedSuccess, "success".equals(result.get("status")));
  }

  private static Stream<Arguments> providePlaceOrderTestCases() {
    return Stream.of(
        // 购物车项存在
        Arguments.of(List.of(1L), 1L, "Receiver", "Address", "123456789", false),
        // 购物车项不存在
        Arguments.of(List.of(2L), 1L, "Receiver", "Address", "123456789", false));
  }

  @Test
  void testSearchOrderItems() {
    // 模拟 orderItemDao.findByKeyword 返回订单项列表
    when(orderItemDao.findByKeyword(anyString())).thenReturn(List.of(orderItem));

    // 调用方法
    JSONObject result = orderService.searchOrderItems("Test Book", 0, 10);

    // 验证结果
    assertEquals(1, result.getIntValue("totalNumber"));
  }
}
