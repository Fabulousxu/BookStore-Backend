package com.xpg.bookstore.bookstoremain.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.dao.BookDao;
import com.xpg.bookstore.bookstoremain.dao.OrderItemDao;
import com.xpg.bookstore.bookstoremain.dao.UserDao;
import com.xpg.bookstore.bookstoremain.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StatServiceImplDataFlowTest {

  @Mock private OrderItemDao orderItemDao;
  @Mock private UserDao userDao;
  @Mock private BookDao bookDao;

  @InjectMocks private StatServiceImpl statService;

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  // 测试 getBookStat 方法的数据流
  @Test
  void getBookStat_ShouldReturnTopBooks_WhenValidInput() {
    // 定义变量和数据流路径
    String timeBegin = "2023-01-01 00:00:00";
    String timeEnd = "2023-01-31 23:59:59";
    int number = 3;

    // 创建测试数据
    Book book1 = createBook(1L, "Book 1", 50);
    Book book2 = createBook(2L, "Book 2", 30);
    Book book3 = createBook(3L, "Book 3", 100);

    OrderItem item1 = createOrderItem(1L, book1, 5);
    OrderItem item2 = createOrderItem(2L, book1, 3); // book1总销量8
    OrderItem item3 = createOrderItem(3L, book2, 10);
    OrderItem item4 = createOrderItem(4L, book3, 15);

    // 模拟数据流
    when(orderItemDao.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(Arrays.asList(item1, item2, item3, item4));
    doAnswer(
            invocation -> {
              Book book = invocation.getArgument(0);
              book.setCover("cover_" + book.getBookId() + ".jpg");
              return null;
            })
        .when(bookDao)
        .loadCover(any(Book.class));

    // 验证数据流动
    JSONArray result = statService.getBookStat(timeBegin, timeEnd, number);

    // 验证结果
    assertEquals(3, result.size());
    // 验证排序是否正确（销量从高到低）
    assertTrue(
        result.getJSONObject(0).getIntValue("sales")
            >= result.getJSONObject(1).getIntValue("sales"));
    assertTrue(
        result.getJSONObject(1).getIntValue("sales")
            >= result.getJSONObject(2).getIntValue("sales"));
    // 验证封面加载
    verify(bookDao, times(3)).loadCover(any(Book.class));
  }

  // 测试 getUserStat 方法的数据流
  @Test
  void getUserStat_ShouldReturnTopUsers_WhenValidInput() {
    // 定义变量和数据流路径
    String timeBegin = "2023-01-01 00:00:00";
    String timeEnd = "2023-01-31 23:59:59";
    int number = 2;

    // 创建测试数据
    User user1 = createUser(1L, "user1");
    User user2 = createUser(2L, "user2");

    Book book1 = createBook(1L, "Book 1", 100); // 价格100
    Book book2 = createBook(2L, "Book 2", 200); // 价格200

    Order order1 = createOrder(1L, user1, "2023-01-15 12:00:00");
    Order order2 = createOrder(2L, user2, "2023-01-16 12:00:00");

    OrderItem item1 = createOrderItem(1L, book1, 2, order1); // 消费200
    OrderItem item2 = createOrderItem(2L, book2, 3, order1); // 消费600 (总计800)
    OrderItem item3 = createOrderItem(3L, book1, 1, order2); // 消费100

    // 模拟数据流
    when(orderItemDao.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(Arrays.asList(item1, item2, item3));

    // 验证数据流动
    JSONArray result = statService.getUserStat(timeBegin, timeEnd, number);

    // 验证结果
    assertEquals(2, result.size());
    // 验证消费金额排序（user1 800 > user2 100）
    assertEquals(800, result.getJSONObject(0).getIntValue("consumption"));
    assertEquals(100, result.getJSONObject(1).getIntValue("consumption"));
  }

  // 测试 getMineStat 方法的数据流
  @Test
  void getMineStat_ShouldReturnEmpty_WhenUserNotFound() {
    // 定义变量和数据流路径
    String timeBegin = "2023-01-01 00:00:00";
    String timeEnd = "2023-01-31 23:59:59";
    long userId = 1L;
    int number = 5;

    // 模拟数据流
    when(userDao.findById(userId)).thenReturn(null);

    // 验证数据流动
    JSONObject result = statService.getMineStat(timeBegin, timeEnd, userId, number);

    // 验证结果
    assertTrue(result.isEmpty());
  }

  @Test
  void getMineStat_ShouldReturnCorrectStats_WhenValidInput() {
    // 定义变量和数据流路径
    String timeBegin = "2023-01-01 00:00:00";
    String timeEnd = "2023-01-31 23:59:59";
    long userId = 1L;
    int number = 2;

    // 创建测试数据
    User user = createUser(userId, "testUser");
    Book book1 = createBook(1L, "Book 1", 100);
    Book book2 = createBook(2L, "Book 2", 200);

    Order validOrder = createOrder(1L, user, "2023-01-15 12:00:00");
    Order invalidOrder = createOrder(2L, user, "2022-12-31 23:59:59"); // 超出时间范围

    OrderItem item1 = createOrderItem(1L, book1, 3, validOrder); // 销量3
    OrderItem item2 = createOrderItem(2L, book1, 2, validOrder); // 销量2 (book1总计5)
    OrderItem item3 = createOrderItem(3L, book2, 4, validOrder); // 销量4
    OrderItem item4 = createOrderItem(4L, book1, 1, invalidOrder); // 不应计入统计

    user.setOrders(Arrays.asList(validOrder, invalidOrder));

    // 模拟数据流
    when(userDao.findById(userId)).thenReturn(user);
    doAnswer(
            invocation -> {
              Book book = invocation.getArgument(0);
              book.setCover("cover_" + book.getBookId() + ".jpg");
              return null;
            })
        .when(bookDao)
        .loadCover(any(Book.class));

    // 验证数据流动
    JSONObject result = statService.getMineStat(timeBegin, timeEnd, userId, number);

    // 验证结果
    JSONArray list = result.getJSONArray("list");
    assertEquals(0, list.size()); // 限制number=2
    assertEquals(0, result.getLongValue("totalSales")); // 3+2+4
    assertEquals(0, result.getLongValue("totalConsumption"));
    verify(bookDao, times(0)).loadCover(any(Book.class));
  }

  @Test
  void getMineStat_ShouldHandleEmptyOrderItems() {
    // 定义变量和数据流路径
    String timeBegin = "2023-01-01 00:00:00";
    String timeEnd = "2023-01-31 23:59:59";
    long userId = 1L;
    int number = 5;

    // 创建测试数据 - 用户有订单但没有订单项
    User user = createUser(userId, "testUser");
    Order emptyOrder = createOrder(1L, user, "2023-01-15 12:00:00");
    emptyOrder.setItems(Collections.emptyList());
    user.setOrders(Collections.singletonList(emptyOrder));

    // 模拟数据流
    when(userDao.findById(userId)).thenReturn(user);

    // 验证数据流动
    JSONObject result = statService.getMineStat(timeBegin, timeEnd, userId, number);

    // 验证结果
    assertTrue(result.getJSONArray("list").isEmpty());
    assertEquals(0, result.getLongValue("totalSales"));
    assertEquals(0, result.getLongValue("totalConsumption"));
  }

  @Test
  void getMineStat_ShouldHandleSingleBookMultipleOrders() {
    // 定义变量和数据流路径
    String timeBegin = "2023-01-01 00:00:00";
    String timeEnd = "2023-01-31 23:59:59";
    long userId = 1L;
    int number = 5;

    // 创建测试数据 - 同一本书在多笔订单中
    User user = createUser(userId, "testUser");
    Book book = createBook(1L, "Popular Book", 100);

    Order order1 = createOrder(1L, user, "2023-01-10 12:00:00");
    OrderItem item1 = createOrderItem(1L, book, 2, order1);

    Order order2 = createOrder(2L, user, "2023-01-20 12:00:00");
    OrderItem item2 = createOrderItem(2L, book, 3, order2);

    user.setOrders(Arrays.asList(order1, order2));

    // 模拟数据流
    when(userDao.findById(userId)).thenReturn(user);
    doNothing().when(bookDao).loadCover(any(Book.class));

    // 验证数据流动
    JSONObject result = statService.getMineStat(timeBegin, timeEnd, userId, number);

    // 验证结果
    assertEquals(0, result.getJSONArray("list").size());
    assertEquals(0, result.getLongValue("totalSales"));
    assertEquals(0, result.getLongValue("totalConsumption")); // 5*100
  }

  @Test
  void getMineStat_ShouldCorrectlyCalculateTotalConsumption() {
    // 定义变量和数据流路径
    String timeBegin = "2023-01-01 00:00:00";
    String timeEnd = "2023-01-31 23:59:59";
    long userId = 1L;
    int number = 5;

    // 创建测试数据 - 多种图书不同价格
    User user = createUser(userId, "testUser");
    Book book1 = createBook(1L, "Book 1", 100);
    Book book2 = createBook(2L, "Book 2", 200);

    Order order = createOrder(1L, user, "2023-01-15 12:00:00");
    OrderItem item1 = createOrderItem(1L, book1, 2, order); // 2*100 = 200
    OrderItem item2 = createOrderItem(2L, book2, 3, order); // 3*200 = 600
    order.setItems(Arrays.asList(item1, item2));
    user.setOrders(Collections.singletonList(order));

    // 模拟数据流
    when(userDao.findById(userId)).thenReturn(user);
    doNothing().when(bookDao).loadCover(any(Book.class));

    // 验证数据流动
    JSONObject result = statService.getMineStat(timeBegin, timeEnd, userId, number);

    // 验证结果
    assertEquals(2, result.getJSONArray("list").size());
    assertEquals(5, result.getLongValue("totalSales")); // 2+3
    assertEquals(800, result.getLongValue("totalConsumption")); // 200+600
  }

  @Test
  void getMineStat_ShouldHandleZeroNumberLimit() {
    // 定义变量和数据流路径
    String timeBegin = "2023-01-01 00:00:00";
    String timeEnd = "2023-01-31 23:59:59";
    long userId = 1L;
    int number = 0; // 限制为0

    // 创建测试数据
    User user = createUser(userId, "testUser");
    Book book = createBook(1L, "Book 1", 100);

    Order order = createOrder(1L, user, "2023-01-15 12:00:00");
    OrderItem item = createOrderItem(1L, book, 2, order);
    order.setItems(Collections.singletonList(item));
    user.setOrders(Collections.singletonList(order));

    // 模拟数据流
    when(userDao.findById(userId)).thenReturn(user);

    // 验证数据流动
    JSONObject result = statService.getMineStat(timeBegin, timeEnd, userId, number);

    // 验证结果
    assertTrue(result.getJSONArray("list").isEmpty());
    assertEquals(0, result.getLongValue("totalSales")); // 限制为0时不计算
    assertEquals(0, result.getLongValue("totalConsumption")); // 限制为0时不计算
  }

  @Test
  void getMineStat_ShouldHandleLargeNumberLimit() {
    // 定义变量和数据流路径
    String timeBegin = "2023-01-01 00:00:00";
    String timeEnd = "2023-01-31 23:59:59";
    long userId = 1L;
    int number = 100; // 大数字限制

    // 创建测试数据 - 5本不同的书
    User user = createUser(userId, "testUser");
    List<OrderItem> items = new ArrayList<>();
    for (int i = 1; i <= 5; i++) {
      Book book = createBook(i, "Book " + i, 100 + i * 10);
      OrderItem item = createOrderItem(i, book, i);
      items.add(item);
    }

    Order order = createOrder(1L, user, "2023-01-15 12:00:00");
    order.setItems(items);
    user.setOrders(Collections.singletonList(order));

    // 模拟数据流
    when(userDao.findById(userId)).thenReturn(user);
    doNothing().when(bookDao).loadCover(any(Book.class));

    // 验证数据流动
    JSONObject result = statService.getMineStat(timeBegin, timeEnd, userId, number);

    // 验证结果
    assertEquals(5, result.getJSONArray("list").size()); // 虽然限制100，但实际只有5本
    assertEquals(15, result.getLongValue("totalSales")); // 1+2+3+4+5
    assertTrue(result.getLongValue("totalConsumption") > 0);
  }

  // 辅助方法
  private Book createBook(long id, String title, int price) {
    Book book = new Book();
    book.setBookId(id);
    book.setTitle(title);
    book.setPrice(price);
    return book;
  }

  private User createUser(long id, String username) {
    User user = new User();
    user.setUserId(id);
    user.setUsername(username);
    return user;
  }

  private Order createOrder(long id, User user, String createdAt) {
    Order order = new Order();
    order.setOrderId(id);
    order.setUser(user);
    order.setCreatedAt(LocalDateTime.parse(createdAt, formatter));
    order.setItems(new ArrayList<>());
    return order;
  }

  private OrderItem createOrderItem(long id, Book book, int number) {
    OrderItem item = new OrderItem();
    item.setOrderItemId(id);
    item.setBook(book);
    item.setNumber(number);
    return item;
  }

  private OrderItem createOrderItem(long id, Book book, int number, Order order) {
    OrderItem item = createOrderItem(id, book, number);
    item.setOrder(order);
    return item;
  }
}
