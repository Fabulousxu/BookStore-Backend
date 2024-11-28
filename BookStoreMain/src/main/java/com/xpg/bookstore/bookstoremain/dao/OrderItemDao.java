package com.xpg.bookstore.bookstoremain.dao;

import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.entity.OrderItem;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemDao {
  OrderItem save(OrderItem orderItem);

  List<OrderItem> findByKeyword(String keyword);

  List<OrderItem> findByCreatedAtBetween(LocalDateTime timeBegin, LocalDateTime timeEnd);

  JSONObject loadOrderToJson(OrderItem orderItem);
}
