package com.xpg.bookstore.bookstoremain.dao.impl;

import com.xpg.bookstore.bookstoremain.dao.OrderDao;
import com.xpg.bookstore.bookstoremain.repository.OrderRepository;
import com.xpg.bookstore.bookstoremain.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class OrderDaoImpl implements OrderDao {
  @Autowired private OrderRepository orderRepository;

  @Override
  @Transactional
  public Order save(Order order) {
    var res = orderRepository.save(order);
//    int error = 1 / 0;
    return res;
  }
}
