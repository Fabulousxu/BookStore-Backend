package com.xpg.bookstore.bookstoremain.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.dao.BookDao;
import com.xpg.bookstore.bookstoremain.dao.OrderItemDao;
import com.xpg.bookstore.bookstoremain.dao.UserDao;
import com.xpg.bookstore.bookstoremain.entity.Book;
import com.xpg.bookstore.bookstoremain.entity.Order;
import com.xpg.bookstore.bookstoremain.entity.OrderItem;
import com.xpg.bookstore.bookstoremain.entity.User;
import org.json.JSONException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatServiceImplAITest {

  @Mock private OrderItemDao orderItemDao;

  @Mock private UserDao userDao;

  @Mock private BookDao bookDao;

  @InjectMocks private StatServiceImpl statService;

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  @ParameterizedTest
  @MethodSource("provideBookStatTestCases")
  void testGetBookStat(
      String timeBegin,
      String timeEnd,
      int number,
      List<OrderItem> orderItems,
      JSONArray expectedResult) {
    when(orderItemDao.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(orderItems);

    JSONArray result = statService.getBookStat(timeBegin, timeEnd, number);

    assertEquals(expectedResult, result);
    verify(orderItemDao, times(1))
        .findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class));
  }

  private static Stream<Arguments> provideBookStatTestCases() {
    Book book1 = new Book();
    book1.setBookId(1L);
    book1.setTitle("Book 1");
    book1.setPrice(100);

    Book book2 = new Book();
    book2.setBookId(2L);
    book2.setTitle("Book 2");
    book2.setPrice(200);

    OrderItem orderItem1 = new OrderItem();
    orderItem1.setBook(book1);
    orderItem1.setNumber(2);

    OrderItem orderItem2 = new OrderItem();
    orderItem2.setBook(book2);
    orderItem2.setNumber(3);

    JSONArray expectedResult = new JSONArray();
    book2.setSales(3);
    expectedResult.add(book2);
    book1.setSales(2);
    expectedResult.add(book1);

    return Stream.of(
        Arguments.of(
            "2023-01-01 00:00:00",
            "2023-01-31 23:59:59",
            2,
            Arrays.asList(orderItem1, orderItem2),
            expectedResult));
  }

  @ParameterizedTest
  @MethodSource("provideMineStatTestCases")
  void testGetMineStat(
      String timeBegin,
      String timeEnd,
      long userId,
      int number,
      User user,
      JSONObject expectedResult) {
    when(userDao.findById(userId)).thenReturn(user);

    JSONObject result = statService.getMineStat(timeBegin, timeEnd, userId, number);

    assertEquals(expectedResult.toJSONString(), result.toJSONString());
    verify(userDao, times(1)).findById(userId);
  }

  private static Stream<Arguments> provideMineStatTestCases() {
    User user = new User();
    user.setUserId(1L);
    user.setUsername("User 1");

    Book book1 = new Book();
    book1.setBookId(1L);
    book1.setTitle("Book 1");
    book1.setPrice(100);

    Book book2 = new Book();
    book2.setBookId(2L);
    book2.setTitle("Book 2");
    book2.setPrice(200);

    Order order = new Order();
    order.setUser(user);
    order.setCreatedAt(
        LocalDateTime.parse(
            "2023-01-15 12:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

    OrderItem orderItem1 = new OrderItem();
    orderItem1.setBook(book1);
    orderItem1.setNumber(2);
    orderItem1.setOrder(order);

    OrderItem orderItem2 = new OrderItem();
    orderItem2.setBook(book2);
    orderItem2.setNumber(3);
    orderItem2.setOrder(order);

    order.setItems(Arrays.asList(orderItem1, orderItem2));
    user.setOrders(Collections.singletonList(order));

    JSONArray bookStatArray = new JSONArray();
    book2.setSales(3);
    bookStatArray.add(book2);
    book1.setSales(2);
    bookStatArray.add(book1);

    JSONObject expectedResult = new JSONObject();
    expectedResult.put("list", bookStatArray);
    expectedResult.put("totalSales", 5);
    expectedResult.put("totalConsumption", 800);

    return Stream.of(
        Arguments.of("2023-01-01 00:00:00", "2023-01-31 23:59:59", 1L, 2, user, expectedResult));
  }

  @ParameterizedTest
  @MethodSource("provideUserStatTestCases")
  void testGetUserStat(
      String timeBegin,
      String timeEnd,
      int number,
      List<OrderItem> orderItems,
      JSONArray expectedResult)
      throws JSONException {
    when(orderItemDao.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(orderItems);

    JSONArray result = statService.getUserStat(timeBegin, timeEnd, number);

    JSONAssert.assertEquals(
        expectedResult.toJSONString(), result.toJSONString(), false); // 设置为 false 以忽略额外字段
    verify(orderItemDao, times(1))
        .findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class));
  }

  private static Stream<Arguments> provideUserStatTestCases() {
    User user1 = new User();
    user1.setUserId(1L);
    user1.setUsername("User 1");

    User user2 = new User();
    user2.setUserId(2L);
    user2.setUsername("User 2");

    Book book1 = new Book();
    book1.setBookId(1L);
    book1.setTitle("Book 1");
    book1.setPrice(100);

    Book book2 = new Book();
    book2.setBookId(2L);
    book2.setTitle("Book 2");
    book2.setPrice(200);

    Order order1 = new Order();
    order1.setUser(user1);

    Order order2 = new Order();
    order2.setUser(user2);

    OrderItem orderItem1 = new OrderItem();
    orderItem1.setBook(book1);
    orderItem1.setNumber(2);
    orderItem1.setOrder(order1);

    OrderItem orderItem2 = new OrderItem();
    orderItem2.setBook(book2);
    orderItem2.setNumber(3);
    orderItem2.setOrder(order2);

    JSONObject userStat1 = new JSONObject();
    userStat1.put("id", user1.getUserId());
    userStat1.put("username", user1.getUsername());
    userStat1.put("balance", 0);
    userStat1.put("nickname", "");
    userStat1.put("silence", false);
    userStat1.put("consumption", 200);

    JSONObject userStat2 = new JSONObject();
    userStat2.put("id", user2.getUserId());
    userStat2.put("username", user2.getUsername());
    userStat2.put("balance", 0);
    userStat2.put("nickname", "");
    userStat2.put("silence", false);
    userStat2.put("consumption", 600);

    JSONArray expectedResult = new JSONArray();
    expectedResult.add(userStat2);
    expectedResult.add(userStat1);

    return Stream.of(
        Arguments.of(
            "2023-01-01 00:00:00",
            "2023-01-31 23:59:59",
            2,
            Arrays.asList(orderItem1, orderItem2),
            expectedResult));
  }
}
