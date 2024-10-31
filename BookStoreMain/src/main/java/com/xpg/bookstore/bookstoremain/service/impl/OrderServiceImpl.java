package com.xpg.bookstore.bookstoremain.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.dao.BookDao;
import com.xpg.bookstore.bookstoremain.dao.OrderDao;
import com.xpg.bookstore.bookstoremain.dao.OrderItemDao;
import com.xpg.bookstore.bookstoremain.dao.UserDao;
import com.xpg.bookstore.bookstoremain.entity.*;
import com.xpg.bookstore.bookstoremain.service.OrderService;
import com.xpg.bookstore.bookstoremain.util.Util;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  @Autowired private BookDao bookDao;
  @Autowired private OrderDao orderDao;
  @Autowired private OrderItemDao orderItemDao;
  @Autowired private UserDao userDao;

  @Override
  public JSONArray getOrderItems(long userId, String keyword) {
    boolean timeFlag = true;
    int timeIndex = keyword.indexOf("time:");
    if (keyword.length() - timeIndex < 44) timeFlag = false;
    LocalDateTime begin = null, end = null;
    if (timeFlag) {
      begin = LocalDateTime.parse(keyword.substring(timeIndex + 5, timeIndex + 24), formatter);
      end = LocalDateTime.parse(keyword.substring(timeIndex + 25, timeIndex + 44), formatter);
      keyword = keyword.substring(0, timeIndex).trim();
    }

    JSONArray res = new JSONArray();
    User user = userDao.findById(userId);
    if (user != null) {
      for (Order order : user.getOrders()) {
        if (timeFlag && (order.getCreatedAt().isBefore(begin) || order.getCreatedAt().isAfter(end)))
          continue;
        if (order.getReceiver().contains(keyword)
            || order.getAddress().contains(keyword)
            || order.getTel().contains(keyword)) {
          for (OrderItem orderItem : order.getItems()) res.add(orderItem.toJsonWithOrderMessage());
        } else
          for (OrderItem orderItem : order.getItems())
            if (orderItem.getBook().getTitle().contains(keyword)
                || orderItem.getBook().getAuthor().contains(keyword)
                || orderItem.getBook().getIsbn().contains(keyword)
                || orderItem.getBook().getDescription().contains(keyword))
              res.add(orderItem.toJsonWithOrderMessage());
      }
    }
    return res;
  }

  @Override
  @Transactional
  public JSONObject placeOrder(
      List<Long> cartItemIds, long userId, String receiver, String address, String tel) {
    User user = userDao.findById(userId);
    if (user == null) return Util.errorResponseJson("用户不存在");
    for (long cartItemId : cartItemIds)
      if (user.getCart().stream().noneMatch(item -> item.getCartItemId() == cartItemId))
        return Util.errorResponseJson("购物车商品错误");
    Order order = new Order(user, receiver, address, tel);
    orderDao.save(order);
    // int error = 1 / 0;
    for (long cartItemId : cartItemIds) {
      CartItem cartItem =
          user.getCart().stream()
              .filter(item -> item.getCartItemId() == cartItemId)
              .findFirst()
              .get();
      Book book = cartItem.getBook();
      book.setSales(book.getSales() + cartItem.getNumber());
      book.setRepertory(book.getRepertory() - cartItem.getNumber());
      // int error = 1 / 0;
      try {
        orderItemDao.save(new OrderItem(order, book, cartItem.getNumber()));
      } catch (Exception e) {
        e.printStackTrace();
      }
      // int error = 1 / 0;
      user.getCart().remove(cartItem);
      bookDao.save(book);
    }
    userDao.save(user);
    return Util.successResponseJson("下单成功!");
  }

  @Override
  public JSONObject searchOrderItems(String keyword, int pageIndex, int pageSize) {
    boolean timeFlag = true;
    int timeIndex = keyword.indexOf("time:");
    if (keyword.length() - timeIndex < 44) timeFlag = false;
    LocalDateTime begin = null, end = null;
    if (timeFlag) {
      begin = LocalDateTime.parse(keyword.substring(timeIndex + 5, timeIndex + 24), formatter);
      end = LocalDateTime.parse(keyword.substring(timeIndex + 25, timeIndex + 44), formatter);
      keyword = keyword.substring(0, timeIndex).trim();
    }

    JSONObject res = new JSONObject();
    List<OrderItem> orderItems = orderItemDao.findByKeyword(keyword);
    List<OrderItem> orderItemsByTime = new ArrayList<>();
    for (OrderItem orderItem : orderItems) {
      if (timeFlag) {
        if (orderItem.getOrder().getCreatedAt().isAfter(begin)
            && orderItem.getOrder().getCreatedAt().isBefore(end)) orderItemsByTime.add(orderItem);
      } else orderItemsByTime.add(orderItem);
    }

    res.put("totalNumber", orderItemsByTime.size());
    res.put("totalPage", Math.ceil((double) orderItemsByTime.size() / (double) pageSize));
    JSONArray items = new JSONArray();
    for (int i = pageIndex * pageSize;
        i < orderItemsByTime.size() && i < (pageIndex + 1) * pageSize;
        i++) items.add(orderItemsByTime.get(i).toJsonWithOrderMessage());
    res.put("items", items);
    return res;
  }
}