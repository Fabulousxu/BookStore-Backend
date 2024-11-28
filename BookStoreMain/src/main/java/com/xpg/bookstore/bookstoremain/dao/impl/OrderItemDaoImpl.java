package com.xpg.bookstore.bookstoremain.dao.impl;

import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.dao.BookDao;
import com.xpg.bookstore.bookstoremain.dao.OrderItemDao;
import com.xpg.bookstore.bookstoremain.entity.OrderItem;
import com.xpg.bookstore.bookstoremain.repository.OrderItemRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class OrderItemDaoImpl implements OrderItemDao {
  @Autowired private OrderItemRepository orderItemRepository;
  @Autowired private BookDao bookDao;

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
  public List<OrderItem> findByCreatedAtBetween(LocalDateTime timeBegin, LocalDateTime timeEnd) {
    return orderItemRepository.findByOrder_CreatedAtBetween(timeBegin, timeEnd);
  }

  @Override
  public JSONObject loadOrderToJson(OrderItem orderItem) {
    JSONObject json = JSONObject.from(orderItem);
    json.put("username", orderItem.getOrder().getUser().getUsername());
    json.put("receiver", orderItem.getOrder().getReceiver());
    json.put("address", orderItem.getOrder().getAddress());
    json.put("tel", orderItem.getOrder().getTel());
    json.put("createdAt", orderItem.getOrder().getCreatedAt());
    return json;
  }
}
