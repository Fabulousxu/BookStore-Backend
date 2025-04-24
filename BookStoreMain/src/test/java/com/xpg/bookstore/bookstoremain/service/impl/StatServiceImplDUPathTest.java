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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatServiceImplDUPathTest {
  @InjectMocks private StatServiceImpl statService;
  @Mock private OrderItemDao orderItemDao;
  @Mock private BookDao bookDao;
  @Mock private UserDao userDao;

  @Test
  void getBookStat() {
    Book book1 = new Book();
    book1.setBookId(1);
    Book book2 = new Book();
    book2.setBookId(2);
    OrderItem orderItem1 = new OrderItem();
    orderItem1.setBook(book1);
    orderItem1.setNumber(1);
    OrderItem orderItem2 = new OrderItem();
    orderItem2.setBook(book2);
    orderItem2.setNumber(2);
    when(orderItemDao.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(List.of(orderItem1, orderItem2));

    JSONArray result = statService.getBookStat("2025-01-01 12:00:00", "2025-01-01 13:00:00", 10);
    assertEquals(2, result.size());
  }

  @Test
  void getUserStat() {
    Book book1 = new Book();
    book1.setBookId(1);
    book1.setPrice(1);
    Book book2 = new Book();
    book2.setBookId(2);
    book2.setPrice(2);
    User user1 = new User();
    user1.setUserId(1);
    User user2 = new User();
    user2.setUserId(2);
    Order order1 = new Order();
    order1.setUser(user1);
    Order order2 = new Order();
    order2.setUser(user2);
    OrderItem orderItem1 = new OrderItem();
    orderItem1.setBook(book1);
    orderItem1.setOrder(order1);
    orderItem1.setNumber(1);
    OrderItem orderItem2 = new OrderItem();
    orderItem2.setBook(book2);
    orderItem2.setOrder(order2);
    orderItem2.setNumber(2);
    when(orderItemDao.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(List.of(orderItem1, orderItem2));

    JSONArray result = statService.getUserStat("2025-01-01 12:00:00", "2025-01-01 13:00:00", 10);
    assertEquals(2, result.size());
  }

  @Test
  void getMineStat() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    User user = new User();
    Book book1 = new Book();
    book1.setBookId(1);
    book1.setPrice(1);
    Book book2 = new Book();
    book2.setBookId(2);
    book2.setPrice(2);
    Order order1 = new Order();
    order1.setUser(user);
    order1.setCreatedAt(LocalDateTime.parse("2025-01-01 12:00:00", formatter));
    Order order2 = new Order();
    order2.setUser(user);
    order2.setCreatedAt(LocalDateTime.parse("2025-01-01 13:00:00", formatter));
    OrderItem orderItem1 = new OrderItem();
    orderItem1.setBook(book1);
    orderItem1.setOrder(order1);
    orderItem1.setNumber(1);
    order1.setItems(List.of(orderItem1));
    OrderItem orderItem2 = new OrderItem();
    orderItem2.setBook(book2);
    orderItem2.setOrder(order2);
    orderItem2.setNumber(2);
    order2.setItems(List.of(orderItem2));
    user.setOrders(List.of(order1, order2));
    when(userDao.findById(1L)).thenReturn(user);
    when(userDao.findById(2L)).thenReturn(null);

    JSONObject noneUserResult =
        statService.getMineStat("2025-01-01 12:00:00", "2025-01-01 13:00:00", 2, 10);
    assertTrue(noneUserResult.isEmpty());
    JSONObject noneBookResult =
        statService.getMineStat("2025-01-01 10:00:00", "2025-01-01 11:00:00", 1, 10);
    assertEquals(0, noneBookResult.getJSONArray("list").size());
    assertEquals(0, noneBookResult.getInteger("totalSales"));
    assertEquals(0, noneBookResult.getInteger("totalConsumption"));
    JSONObject allBookResult =
        statService.getMineStat("2025-01-01 11:00:00", "2025-01-01 14:00:00", 1, 10);
    assertEquals(2, allBookResult.getJSONArray("list").size());
    assertEquals(3, allBookResult.getInteger("totalSales"));
    assertEquals(5, allBookResult.getInteger("totalConsumption"));
    JSONObject beforeTimeResult =
        statService.getMineStat("2025-01-01 11:00:00", "2025-01-01 12:00:01", 1, 10);
    assertEquals(1, beforeTimeResult.getJSONArray("list").size());
    assertEquals(1, beforeTimeResult.getInteger("totalSales"));
    assertEquals(1, beforeTimeResult.getInteger("totalConsumption"));
    JSONObject afterTimeResult =
        statService.getMineStat("2025-01-01 12:59:59", "2025-01-01 13:00:01", 1, 10);
    assertEquals(1, afterTimeResult.getJSONArray("list").size());
    assertEquals(2, afterTimeResult.getInteger("totalSales"));
    assertEquals(4, afterTimeResult.getInteger("totalConsumption"));
  }
}
