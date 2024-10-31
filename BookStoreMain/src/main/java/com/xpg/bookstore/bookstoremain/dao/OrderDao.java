package com.xpg.bookstore.bookstoremain.dao;

import com.xpg.bookstore.bookstoremain.entity.Order;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDao {
  Order save(Order order);
}
