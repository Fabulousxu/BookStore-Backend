package com.example.bookstore.dao;

import com.example.bookstore.entity.Order;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDao {
  Order save(Order order);
}
