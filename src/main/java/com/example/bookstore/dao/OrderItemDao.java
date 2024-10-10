package com.example.bookstore.dao;

import com.example.bookstore.entity.OrderItem;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemDao {
  void save(OrderItem orderItem);
}
