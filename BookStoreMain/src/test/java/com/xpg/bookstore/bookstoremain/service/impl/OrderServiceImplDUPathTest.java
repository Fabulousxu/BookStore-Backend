package com.xpg.bookstore.bookstoremain.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.dao.BookDao;
import com.xpg.bookstore.bookstoremain.dao.OrderDao;
import com.xpg.bookstore.bookstoremain.dao.OrderItemDao;
import com.xpg.bookstore.bookstoremain.dao.UserDao;
import com.xpg.bookstore.bookstoremain.entity.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplDUPathTest {
  @InjectMocks private OrderServiceImpl orderService;
  @Mock private BookDao bookDao;
  @Mock private OrderDao orderDao;
  @Mock private OrderItemDao orderItemDao;
  @Mock private UserDao userDao;

  @Test
  void getOrderItems() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    Book javaBook = new Book();
    javaBook.setTitle("Java");
    javaBook.setAuthor("Zhang San");
    javaBook.setIsbn("123456789");
    javaBook.setDescription("Shanghai");
    Book pythonBook = new Book();
    pythonBook.setTitle("Python");
    pythonBook.setAuthor("Li Si");
    pythonBook.setIsbn("987654321");
    pythonBook.setDescription("Beijing");

    OrderItem javaOrderItem = new OrderItem();
    javaOrderItem.setBook(javaBook);
    OrderItem pythonOrderItem = new OrderItem();
    pythonOrderItem.setBook(pythonBook);

    Order order1 = new Order();
    order1.setItems(List.of(pythonOrderItem));
    order1.setReceiver("Zhang San");
    order1.setAddress("Shanghai");
    order1.setTel("123456789");
    order1.setCreatedAt(LocalDateTime.parse("2025-01-01 12:00:00", formatter));
    Order order2 = new Order();
    order2.setItems(List.of(javaOrderItem));
    order2.setReceiver("Li Si");
    order2.setAddress("Beijing");
    order2.setTel("987654321");
    order2.setCreatedAt(LocalDateTime.parse("2025-01-01 13:00:00", formatter));

    User user = new User();
    user.setOrders(List.of(order1, order2));

    when(userDao.findById(eq(1L))).thenReturn(user);
    when(userDao.findById(eq(2L))).thenReturn(null);
    when(orderItemDao.loadOrderToJson(eq(javaOrderItem))).thenReturn(new JSONObject());
    when(orderItemDao.loadOrderToJson(eq(pythonOrderItem))).thenReturn(new JSONObject());

    JSONArray noneUserResult = orderService.getOrderItems(2L, "");
    assertTrue(noneUserResult.isEmpty());
    JSONArray noneResult = orderService.getOrderItems(1L, "NoExistBook");
    assertTrue(noneResult.isEmpty());
    JSONArray timeBeforeResult =
        orderService.getOrderItems(1L, "time:2025-01-01 10:00:00 2025-01-01 11:00:00");
    assertTrue(timeBeforeResult.isEmpty());
    JSONArray timeAfterResult =
        orderService.getOrderItems(1L, "time:2025-01-01 14:00:00 2025-01-01 15:00:00");
    assertTrue(timeAfterResult.isEmpty());
    JSONArray receiverOrAuthorResult =
        orderService.getOrderItems(1L, "Zhang San time:2025-01-01 11:00:00 2025-01-01 14:00:00");
    assertEquals(2, receiverOrAuthorResult.size());
    JSONArray addressAndDescriptionResult =
        orderService.getOrderItems(1L, "Shanghai time:2025-01-01 11:00:00 2025-01-01 14:00:00");
    assertEquals(2, addressAndDescriptionResult.size());
    JSONArray telOrIsbnResult =
        orderService.getOrderItems(1L, "123456789 time:2025-01-01 11:00:00 2025-01-01 14:00:00");
    assertEquals(2, telOrIsbnResult.size());
    JSONArray titleResult =
        orderService.getOrderItems(1L, "Java time:2025-01-01 11:00:00 2025-01-01 14:00:00");
    assertEquals(1, titleResult.size());
  }

  @Test
  void placeOrder() {
    Book book = new Book();
    book.setSales(0);
    book.setRepertory(1);

    List<CartItem> cartItems = new ArrayList<>();
    CartItem cartItem = new CartItem();
    cartItem.setCartItemId(1);
    cartItem.setBook(book);
    cartItem.setNumber(1);
    cartItems.add(cartItem);

    User user = new User();
    user.setCart(cartItems);

    when(userDao.findById(1L)).thenReturn(user);
    when(userDao.findById(2L)).thenReturn(null);
    when(orderDao.save(any(Order.class))).thenReturn(new Order());
    when(orderItemDao.save(any(OrderItem.class))).thenReturn(new OrderItem());
    when(userDao.save(any(User.class))).thenReturn(user);
    when(bookDao.save(any(Book.class))).thenReturn(book);

    JSONObject noneUserResult = orderService.placeOrder(new ArrayList<>(), 2, "", "", "");
    assertFalse(noneUserResult.getBoolean("ok"));
    JSONObject noExistResult = orderService.placeOrder(List.of(2L), 1, "", "", "");
    assertFalse(noExistResult.getBoolean("ok"));
    JSONObject successResult = orderService.placeOrder(List.of(1L), 1, "", "", "");
    assertTrue(successResult.getBoolean("ok"));
    assertEquals(0, book.getRepertory());
    assertEquals(1, book.getSales());

    when(orderItemDao.save(any(OrderItem.class))).thenThrow(new RuntimeException("error"));
    user.getCart().add(cartItem);
    try {
      orderService.placeOrder(List.of(1L), 1, "", "", "");
    } catch (Exception e) {
      assertEquals("error", e.getMessage());
    }
  }

  @Test
  void searchOrderItems() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    Book book1 = new Book();
    Book book2 = new Book();
    Order order1 = new Order();
    order1.setCreatedAt(LocalDateTime.parse("2025-01-01 12:00:00", formatter));
    Order order2 = new Order();
    order2.setCreatedAt(LocalDateTime.parse("2025-01-01 13:00:00", formatter));
    OrderItem orderItem1 = new OrderItem();
    orderItem1.setBook(book1);
    orderItem1.setOrder(order1);
    OrderItem orderItem2 = new OrderItem();
    orderItem2.setBook(book2);
    orderItem2.setOrder(order2);

    when(orderItemDao.findByKeyword(any(String.class))).thenReturn(List.of(orderItem1, orderItem2));
    when(orderItemDao.loadOrderToJson(eq(orderItem1))).thenReturn(new JSONObject());
    when(orderItemDao.loadOrderToJson(eq(orderItem2))).thenReturn(new JSONObject());

    JSONObject noTimeResult = orderService.searchOrderItems("", 0, 10);
    assertEquals(2, noTimeResult.getJSONArray("items").size());
    JSONObject time1Result =
        orderService.searchOrderItems("time:2025-01-01 11:00:00 2025-01-01 12:00:01", 0, 10);
    assertEquals(1, time1Result.getJSONArray("items").size());
    JSONObject time2Result =
        orderService.searchOrderItems("time:2025-01-01 12:59:59 2025-01-01 14:00:00", 0, 10);
    assertEquals(1, time2Result.getJSONArray("items").size());
    JSONObject page2Result =
        orderService.searchOrderItems("time:2025-01-01 11:00:00 2025-01-01 14:00:00", 0, 1);
    assertEquals(1, time2Result.getJSONArray("items").size());
  }
}
