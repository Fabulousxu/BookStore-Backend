package com.example.bookstore.dao.impl;

import com.example.bookstore.dao.OrderItemDao;
import com.example.bookstore.entity.OrderItem;
import com.example.bookstore.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class OrderItemDaoImpl implements OrderItemDao {
  @Autowired private OrderItemRepository orderItemRepository;

  @Override
  @Transactional
  public OrderItem save(OrderItem orderItem) {
    var res = orderItemRepository.save(orderItem);
//    int error = 1 / 0;
    return res;
  }
}
