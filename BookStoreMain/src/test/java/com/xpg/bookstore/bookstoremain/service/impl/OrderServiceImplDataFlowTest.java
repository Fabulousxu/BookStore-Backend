package com.xpg.bookstore.bookstoremain.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.dao.*;
import com.xpg.bookstore.bookstoremain.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class OrderServiceImplDataFlowTest {

  @Mock private BookDao bookDao;
  @Mock private OrderDao orderDao;
  @Mock private OrderItemDao orderItemDao;
  @Mock private UserDao userDao;

  @InjectMocks private OrderServiceImpl orderService;

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  // 测试 getOrderItems 方法的数据流
  @Test
  void getOrderItems_ShouldReturnEmpty_WhenUserNotFound() {
    // 定义变量
    long userId = 1L;
    String keyword = "test";

    // 模拟数据流
    when(userDao.findById(userId)).thenReturn(null);

    // 验证数据流动
    JSONArray result = orderService.getOrderItems(userId, keyword);

    assertTrue(result.isEmpty());
    verify(userDao).findById(userId);
  }

  @Test
  void getOrderItems_ShouldFilterByTimeRange_WhenTimeFlagIsTrue() {
    // 定义变量和数据流路径
    long userId = 1L;
    String keyword = "search time:2023-01-01 00:00:00-2023-01-31 23:59:59";

    User user = new User();
    user.setUserId(userId);

    Order order1 = createOrder(1L, "2023-01-15 12:00:00");
    Order order2 = createOrder(2L, "2023-02-01 12:00:00"); // 超出时间范围

    user.setOrders(Arrays.asList(order1, order2));

    // 模拟数据流
    when(userDao.findById(userId)).thenReturn(user);
    doNothing().when(bookDao).loadCover(any(Book.class));
    when(orderItemDao.loadOrderToJson(any(OrderItem.class))).thenReturn(new JSONObject());

    // 验证数据流动
    JSONArray result = orderService.getOrderItems(userId, keyword);

    assertEquals(0, result.size()); // 只应包含在时间范围内的订单项
    verify(userDao).findById(userId);
  }

  @Test
  void getOrderItems_ShouldFilterByKeyword_WhenTimeFlagIsFalse() {
    // 定义变量和数据流路径
    long userId = 1L;
    String keyword = "Java";

    User user = new User();
    user.setUserId(userId);

    Order order = createOrder(1L, "2023-01-15 12:00:00");
    OrderItem matchingItem = createOrderItem(1L, "Java Programming");
    OrderItem nonMatchingItem = createOrderItem(2L, "Python Programming");

    order.setItems(Arrays.asList(matchingItem, nonMatchingItem));
    user.setOrders(Collections.singletonList(order));

    // 模拟数据流
    when(userDao.findById(userId)).thenReturn(user);
    doNothing().when(bookDao).loadCover(any(Book.class));
    when(orderItemDao.loadOrderToJson(matchingItem))
        .thenReturn(new JSONObject().fluentPut("title", "Java Programming"));

    // 验证数据流动
    JSONArray result = orderService.getOrderItems(userId, keyword);

    assertEquals(1, result.size());
    assertEquals("Java Programming", result.getJSONObject(0).getString("title"));
    verify(userDao).findById(userId);
  }

  // 测试 placeOrder 方法的数据流
  @Test
  void placeOrder_ShouldFail_WhenUserNotFound() {
    // 定义变量
    long userId = 1L;
    List<Long> cartItemIds = Arrays.asList(1L, 2L);

    // 模拟数据流路径
    when(userDao.findById(userId)).thenReturn(null);

    // 验证数据流动
    JSONObject result =
        orderService.placeOrder(cartItemIds, userId, "Receiver", "Address", "123456789");

    assertEquals("用户不存在", result.getString("message"));
    verify(userDao).findById(userId);
  }

  @Test
  void placeOrder_ShouldFail_WhenCartItemNotFound() {
    // 定义变量和数据流路径
    long userId = 1L;
    List<Long> cartItemIds = Arrays.asList(1L, 2L);

    User user = new User();
    user.setUserId(userId);
    user.setCart(new ArrayList<>()); // 空购物车

    // 模拟数据流
    when(userDao.findById(userId)).thenReturn(user);

    // 验证数据流动
    JSONObject result =
        orderService.placeOrder(cartItemIds, userId, "Receiver", "Address", "123456789");

    assertEquals("购物车商品错误", result.getString("message"));
    verify(userDao).findById(userId);
  }

  @Test
  void placeOrder_ShouldSuccess_WhenValidData() {
    // 定义变量和数据流路径
    long userId = 1L;
    long cartItemId = 1L;
    List<Long> cartItemIds = Collections.singletonList(cartItemId);

    User user = new User();
    user.setUserId(userId);

    Book book = new Book();
    book.setBookId(101L);
    book.setSales(10);
    book.setRepertory(100);

    CartItem cartItem = new CartItem();
    cartItem.setCartItemId(cartItemId);
    cartItem.setBook(book);
    cartItem.setNumber(2);

    user.setCart(new ArrayList<>(Collections.singletonList(cartItem)));

    // 模拟数据流
    when(userDao.findById(userId)).thenReturn(user);
    when(orderDao.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(bookDao.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(orderItemDao.save(any(OrderItem.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // 验证数据流动
    JSONObject result =
        orderService.placeOrder(cartItemIds, userId, "Receiver", "Address", "123456789");

    assertEquals("下单成功!", result.getString("message"));
    assertEquals(12, book.getSales()); // 原销售数10 + 购买数2
    assertEquals(98, book.getRepertory()); // 原库存100 - 购买数2
    assertTrue(user.getCart().isEmpty());
    verify(userDao).save(user);
    verify(bookDao).save(book);
  }

  // 测试 searchOrderItems 方法的数据流
  @Test
  void searchOrderItems_ShouldFilterByTimeRange_WhenTimeFlagIsTrue() {
    // 定义变量和数据流路径
    String keyword = "Java time:2023-01-01 00:00:00-2023-01-31 23:59:59";
    int pageIndex = 0;
    int pageSize = 10;

    OrderItem matchingItem = createOrderItem(1L, "Java Programming");
    matchingItem.getOrder().setCreatedAt(LocalDateTime.parse("2023-01-15 12:00:00", formatter));

    OrderItem nonMatchingItem = createOrderItem(2L, "Java Advanced");
    nonMatchingItem.getOrder().setCreatedAt(LocalDateTime.parse("2023-02-01 12:00:00", formatter));

    // 模拟数据流
    when(orderItemDao.findByKeyword("Java"))
        .thenReturn(Arrays.asList(matchingItem, nonMatchingItem));
    doNothing().when(bookDao).loadCover(any(Book.class));
    when(orderItemDao.loadOrderToJson(matchingItem))
        .thenReturn(new JSONObject().fluentPut("title", "Java Programming"));

    // 验证数据流动
    JSONObject result = orderService.searchOrderItems(keyword, pageIndex, pageSize);

    assertEquals(1, result.getJSONArray("items").size());
    assertEquals(
        "Java Programming", result.getJSONArray("items").getJSONObject(0).getString("title"));
  }

  @Test
  void searchOrderItems_ShouldPaginateResultsCorrectly() {
    // 定义变量和数据流路径
    String keyword = "Programming";
    int pageIndex = 1;
    int pageSize = 2;

    List<OrderItem> items = new ArrayList<>();
    for (int i = 1; i <= 5; i++) {
      OrderItem item = createOrderItem(i, "Book " + i);
      items.add(item);
      when(orderItemDao.loadOrderToJson(item))
          .thenReturn(new JSONObject().fluentPut("title", "Book " + i));
    }

    // 模拟数据流
    when(orderItemDao.findByKeyword(keyword)).thenReturn(items);
    doNothing().when(bookDao).loadCover(any(Book.class));

    // 验证数据流动
    JSONObject result = orderService.searchOrderItems(keyword, pageIndex, pageSize);

    assertEquals(5, result.getIntValue("totalNumber"));
    assertEquals(3, result.getIntValue("totalPage")); // 5 items / 2 per page = 3 pages
    assertEquals(2, result.getJSONArray("items").size()); // 第2页应有2个items
  }

  @Test
  void getOrderItems_ShouldMatchOrderInfo_WhenKeywordMatchesReceiver() {
    // 定义变量和数据流路径
    long userId = 1L;
    String keyword = "张";

    User user = new User();
    user.setUserId(userId);

    Order order = createOrder(1L, "2023-01-15 12:00:00");
    order.setReceiver("张三");
    order.setAddress("北京市");
    order.setTel("13800138000");

    OrderItem orderItem = createOrderItem(1L, "Java Programming");
    order.setItems(Collections.singletonList(orderItem));
    user.setOrders(Collections.singletonList(order));

    // 模拟数据流
    when(userDao.findById(userId)).thenReturn(user);
    doNothing().when(bookDao).loadCover(any(Book.class));
    when(orderItemDao.loadOrderToJson(orderItem))
        .thenReturn(new JSONObject().fluentPut("title", "Java Programming"));

    // 验证数据流动 - 关键词匹配收货人
    JSONArray result = orderService.getOrderItems(userId, keyword);

    assertEquals(1, result.size());
    assertEquals("Java Programming", result.getJSONObject(0).getString("title"));
    verify(bookDao).loadCover(orderItem.getBook());
  }

  @Test
  void getOrderItems_ShouldMatchOrderInfo_WhenKeywordMatchesAddress() {
    // 定义变量和数据流路径
    long userId = 1L;
    String keyword = "北京";

    User user = new User();
    user.setUserId(userId);

    Order order = createOrder(1L, "2023-01-15 12:00:00");
    order.setReceiver("张三");
    order.setAddress("北京市海淀区");
    order.setTel("13800138000");

    OrderItem orderItem = createOrderItem(1L, "Java Programming");
    order.setItems(Collections.singletonList(orderItem));
    user.setOrders(Collections.singletonList(order));

    // 模拟数据流
    when(userDao.findById(userId)).thenReturn(user);
    doNothing().when(bookDao).loadCover(any(Book.class));
    when(orderItemDao.loadOrderToJson(orderItem))
        .thenReturn(new JSONObject().fluentPut("title", "Java Programming"));

    // 验证数据流动 - 关键词匹配地址
    JSONArray result = orderService.getOrderItems(userId, keyword);

    assertEquals(1, result.size());
    verify(bookDao).loadCover(orderItem.getBook());
  }

  @Test
  void getOrderItems_ShouldMatchOrderInfo_WhenKeywordMatchesTel() {
    // 定义变量和数据流路径
    long userId = 1L;
    String keyword = "138";

    User user = new User();
    user.setUserId(userId);

    Order order = createOrder(1L, "2023-01-15 12:00:00");
    order.setReceiver("张三");
    order.setAddress("北京市");
    order.setTel("13800138000");

    OrderItem orderItem = createOrderItem(1L, "Java Programming");
    order.setItems(Collections.singletonList(orderItem));
    user.setOrders(Collections.singletonList(order));

    // 模拟数据流
    when(userDao.findById(userId)).thenReturn(user);
    doNothing().when(bookDao).loadCover(any(Book.class));
    when(orderItemDao.loadOrderToJson(orderItem))
        .thenReturn(new JSONObject().fluentPut("title", "Java Programming"));

    // 验证数据流动 - 关键词匹配电话
    JSONArray result = orderService.getOrderItems(userId, keyword);

    assertEquals(1, result.size());
    verify(bookDao).loadCover(orderItem.getBook());
  }

  @Test
  void getOrderItems_ShouldMatchBookInfo_WhenKeywordMatchesTitle() {
    // 定义变量和数据流路径
    long userId = 1L;
    String keyword = "Java";

    User user = new User();
    user.setUserId(userId);

    Order order = createOrder(1L, "2023-01-15 12:00:00");
    order.setReceiver("张三");
    order.setAddress("北京市");
    order.setTel("13800138000");

    OrderItem matchingItem = createOrderItem(1L, "Java Programming");
    OrderItem nonMatchingItem = createOrderItem(2L, "Python Programming");
    order.setItems(Arrays.asList(matchingItem, nonMatchingItem));
    user.setOrders(Collections.singletonList(order));

    // 模拟数据流
    when(userDao.findById(userId)).thenReturn(user);
    doNothing().when(bookDao).loadCover(any(Book.class));
    when(orderItemDao.loadOrderToJson(matchingItem))
        .thenReturn(new JSONObject().fluentPut("title", "Java Programming"));

    // 验证数据流动 - 关键词匹配图书标题
    JSONArray result = orderService.getOrderItems(userId, keyword);

    assertEquals(1, result.size());
    assertEquals("Java Programming", result.getJSONObject(0).getString("title"));
    verify(bookDao, times(1)).loadCover(matchingItem.getBook());
  }

  @Test
  void getOrderItems_ShouldMatchBookInfo_WhenKeywordMatchesAuthor() {
    // 定义变量和数据流路径
    long userId = 1L;
    String keyword = "Martin";

    User user = new User();
    user.setUserId(userId);

    Order order = createOrder(1L, "2023-01-15 12:00:00");

    OrderItem matchingItem = createOrderItem(1L, "Clean Code");
    matchingItem.getBook().setAuthor("Martin Fowler");
    OrderItem nonMatchingItem = createOrderItem(2L, "Design Patterns");
    nonMatchingItem.getBook().setAuthor("Erich Gamma");

    order.setItems(Arrays.asList(matchingItem, nonMatchingItem));
    user.setOrders(Collections.singletonList(order));

    // 模拟数据流
    when(userDao.findById(userId)).thenReturn(user);
    doNothing().when(bookDao).loadCover(any(Book.class));
    when(orderItemDao.loadOrderToJson(matchingItem))
        .thenReturn(new JSONObject().fluentPut("title", "Clean Code"));

    // 验证数据流动 - 关键词匹配图书作者
    JSONArray result = orderService.getOrderItems(userId, keyword);

    assertEquals(1, result.size());
    verify(bookDao).loadCover(matchingItem.getBook());
  }

  @Test
  void getOrderItems_ShouldMatchBookInfo_WhenKeywordMatchesIsbn() {
    // 定义变量和数据流路径
    long userId = 1L;
    String keyword = "978";

    User user = new User();
    user.setUserId(userId);

    Order order = createOrder(1L, "2023-01-15 12:00:00");

    OrderItem matchingItem = createOrderItem(1L, "Effective Java");
    matchingItem.getBook().setIsbn("978-0-13-468599-1");
    OrderItem nonMatchingItem = createOrderItem(2L, "Head First Design Patterns");
    nonMatchingItem.getBook().setIsbn("0-596-00712-4");

    order.setItems(Arrays.asList(matchingItem, nonMatchingItem));
    user.setOrders(Collections.singletonList(order));

    // 模拟数据流
    when(userDao.findById(userId)).thenReturn(user);
    doNothing().when(bookDao).loadCover(any(Book.class));
    when(orderItemDao.loadOrderToJson(matchingItem))
        .thenReturn(new JSONObject().fluentPut("title", "Effective Java"));

    // 验证数据流动 - 关键词匹配图书ISBN
    JSONArray result = orderService.getOrderItems(userId, keyword);

    assertEquals(1, result.size());
    verify(bookDao).loadCover(matchingItem.getBook());
  }

  @Test
  void getOrderItems_ShouldMatchBookInfo_WhenKeywordMatchesDescription() {
    // 定义变量和数据流路径
    long userId = 1L;
    String keyword = "设计模式";

    User user = new User();
    user.setUserId(userId);

    Order order = createOrder(1L, "2023-01-15 12:00:00");

    OrderItem matchingItem = createOrderItem(1L, "Design Patterns");
    matchingItem.getBook().setDescription("深入讲解设计模式");
    OrderItem nonMatchingItem = createOrderItem(2L, "Clean Code");
    nonMatchingItem.getBook().setDescription("如何编写整洁代码");

    order.setItems(Arrays.asList(matchingItem, nonMatchingItem));
    user.setOrders(Collections.singletonList(order));

    // 模拟数据流
    when(userDao.findById(userId)).thenReturn(user);
    doNothing().when(bookDao).loadCover(any(Book.class));
    when(orderItemDao.loadOrderToJson(matchingItem))
        .thenReturn(new JSONObject().fluentPut("title", "Design Patterns"));

    // 验证数据流动 - 关键词匹配图书描述
    JSONArray result = orderService.getOrderItems(userId, keyword);

    assertEquals(1, result.size());
    verify(bookDao).loadCover(matchingItem.getBook());
  }

  @Test
  void getOrderItems_ShouldMatchBookInfo_WhenOnlyIsbnMatches() {
    // 定义变量和数据流路径
    long userId = 1L;
    String keyword = "12345";

    User user = new User();
    user.setUserId(userId);

    Order order = createOrder(1L, "2023-01-15 12:00:00");
    order.setReceiver("李四");
    order.setAddress("上海市");
    order.setTel("13900139000");

    // 创建匹配项（仅ISBN匹配）
    OrderItem matchingItem = createOrderItem(1L, "Book A");
    matchingItem.getBook().setIsbn("978-12345-678-0"); // ISBN包含keyword
    matchingItem.getBook().setTitle("不匹配标题");
    matchingItem.getBook().setAuthor("不匹配作者");
    matchingItem.getBook().setDescription("不匹配描述");

    // 创建不匹配项
    OrderItem nonMatchingItem = createOrderItem(2L, "Book B");
    nonMatchingItem.getBook().setIsbn("978-00000-000-0");

    order.setItems(Arrays.asList(matchingItem, nonMatchingItem));
    user.setOrders(Collections.singletonList(order));

    // 模拟数据流
    when(userDao.findById(userId)).thenReturn(user);
    doNothing().when(bookDao).loadCover(any(Book.class));
    when(orderItemDao.loadOrderToJson(matchingItem))
        .thenReturn(new JSONObject().fluentPut("isbn", "978-12345-678-0"));

    // 验证数据流动 - 仅ISBN匹配
    JSONArray result = orderService.getOrderItems(userId, keyword);

    assertEquals(1, result.size());
    assertEquals("978-12345-678-0", result.getJSONObject(0).getString("isbn"));
    verify(bookDao).loadCover(matchingItem.getBook());
  }

  @Test
  void getOrderItems_ShouldMatchBookInfo_WhenOnlyDescriptionMatches() {
    // 定义变量和数据流路径
    long userId = 1L;
    String keyword = "编程";

    User user = new User();
    user.setUserId(userId);

    Order order = createOrder(1L, "2023-01-15 12:00:00");
    order.setReceiver("王五");
    order.setAddress("广州市");
    order.setTel("13700137000");

    // 创建匹配项（仅描述匹配）
    OrderItem matchingItem = createOrderItem(1L, "Book C");
    matchingItem.getBook().setDescription("一本关于编程的书籍"); // 描述包含keyword
    matchingItem.getBook().setTitle("不匹配标题");
    matchingItem.getBook().setAuthor("不匹配作者");
    matchingItem.getBook().setIsbn("978-00000-000-0");

    // 创建不匹配项
    OrderItem nonMatchingItem = createOrderItem(2L, "Book D");
    nonMatchingItem.getBook().setDescription("一本关于烹饪的书籍");

    order.setItems(Arrays.asList(matchingItem, nonMatchingItem));
    user.setOrders(Collections.singletonList(order));

    // 模拟数据流
    when(userDao.findById(userId)).thenReturn(user);
    doNothing().when(bookDao).loadCover(any(Book.class));
    when(orderItemDao.loadOrderToJson(matchingItem))
        .thenReturn(new JSONObject().fluentPut("description", "一本关于编程的书籍"));

    // 验证数据流动 - 仅描述匹配
    JSONArray result = orderService.getOrderItems(userId, keyword);

    assertEquals(1, result.size());
    assertTrue(result.getJSONObject(0).getString("description").contains(keyword));
    verify(bookDao).loadCover(matchingItem.getBook());
  }

  @Test
  void getOrderItems_ShouldMatchBookInfo_WhenMultipleFieldsMatch() {
    // 定义变量和数据流路径
    long userId = 1L;
    String keyword = "设计";

    User user = new User();
    user.setUserId(userId);

    Order order = createOrder(1L, "2023-01-15 12:00:00");
    order.setReceiver("赵六");
    order.setAddress("深圳市");
    order.setTel("13600136000");

    // 创建匹配项（多个字段匹配）
    OrderItem matchingItem1 = createOrderItem(1L, "设计模式"); // 标题匹配
    matchingItem1.getBook().setIsbn("978-设计模式-001");

    OrderItem matchingItem2 = createOrderItem(2L, "系统架构");
    matchingItem2.getBook().setAuthor("设计大师"); // 作者匹配

    OrderItem matchingItem3 = createOrderItem(3L, "代码整洁");
    matchingItem3.getBook().setDescription("优秀的设计原则"); // 描述匹配

    OrderItem matchingItem4 = createOrderItem(4L, "重构");
    matchingItem4.getBook().setIsbn("978-设计-002"); // ISBN匹配

    OrderItem nonMatchingItem = createOrderItem(5L, "算法导论");

    order.setItems(
        Arrays.asList(matchingItem1, matchingItem2, matchingItem3, matchingItem4, nonMatchingItem));
    user.setOrders(Collections.singletonList(order));

    // 模拟数据流
    when(userDao.findById(userId)).thenReturn(user);
    doNothing().when(bookDao).loadCover(any(Book.class));
    when(orderItemDao.loadOrderToJson(any(OrderItem.class)))
        .thenAnswer(
            invocation -> {
              OrderItem item = invocation.getArgument(0);
              return new JSONObject().fluentPut("title", item.getBook().getTitle());
            });

    // 验证数据流动 - 多个字段匹配
    JSONArray result = orderService.getOrderItems(userId, keyword);

    assertEquals(4, result.size());
    verify(bookDao, times(4)).loadCover(any(Book.class));
  }

  @Test
  void getOrderItems_ShouldNotMatch_WhenNoFieldsMatch() {
    // 定义变量和数据流路径
    long userId = 1L;
    String keyword = "不存在的关键词";

    User user = new User();
    user.setUserId(userId);

    Order order = createOrder(1L, "2023-01-15 12:00:00");
    order.setReceiver("测试用户");
    order.setAddress("测试地址");
    order.setTel("13500135000");

    OrderItem item1 = createOrderItem(1L, "图书1");
    OrderItem item2 = createOrderItem(2L, "图书2");

    order.setItems(Arrays.asList(item1, item2));
    user.setOrders(Collections.singletonList(order));

    // 模拟数据流
    when(userDao.findById(userId)).thenReturn(user);

    // 验证数据流动 - 无任何匹配
    JSONArray result = orderService.getOrderItems(userId, keyword);

    assertTrue(result.isEmpty());
    verify(bookDao, never()).loadCover(any(Book.class));
  }

  // 辅助方法
  private Order createOrder(long orderId, String createdAt) {
    Order order = new Order();
    order.setOrderId(orderId);
    order.setCreatedAt(LocalDateTime.parse(createdAt, formatter));
    order.setReceiver("");
    order.setAddress("");
    order.setTel("");
    order.setItems(new ArrayList<>());
    return order;
  }

  private OrderItem createOrderItem(long itemId, String bookTitle) {
    OrderItem item = new OrderItem();
    item.setOrderItemId(itemId);

    Order order = new Order();
    order.setOrderId(itemId);
    item.setOrder(order);
    order.setReceiver("");
    order.setAddress("");
    order.setTel("");
    order.setItems(new ArrayList<>());

    Book book = new Book();
    book.setBookId(itemId);
    book.setTitle(bookTitle);
    book.setAuthor("");
    book.setIsbn("");
    book.setDescription("");
    item.setBook(book);

    return item;
  }
}
