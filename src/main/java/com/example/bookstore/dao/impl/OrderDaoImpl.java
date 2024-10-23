package com.example.bookstore.dao.impl;

import com.example.bookstore.dao.OrderDao;
import com.example.bookstore.entity.Order;
import com.example.bookstore.repository.OrderRepository;
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
