package com.example.bookstore.dao.impl;

import com.example.bookstore.dao.OrderItemDao;
import com.example.bookstore.entity.OrderItem;
import com.example.bookstore.repository.OrderItemRepository;
import java.time.LocalDateTime;
import java.util.List;
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

  @Override
  public List<OrderItem> findByKeyword(String keyword) {
    return orderItemRepository
        .findAllByOrder_User_UsernameContainsOrOrder_User_NicknameContainsOrOrder_User_EmailContainsOrOrder_ReceiverContainsOrOrder_AddressContainsOrOrder_TelContainsOrBook_TitleContainsOrBook_AuthorContainsOrBook_IsbnContainsOrBook_DescriptionContains(
            keyword, keyword, keyword, keyword, keyword, keyword, keyword, keyword, keyword,
            keyword);
  }

  @Override
  public List<OrderItem> findByCreatedAtBetween(
      LocalDateTime timeBegin, LocalDateTime timeEnd) {
    return orderItemRepository.findByOrder_CreatedAtBetween(timeBegin, timeEnd);
  }
}
