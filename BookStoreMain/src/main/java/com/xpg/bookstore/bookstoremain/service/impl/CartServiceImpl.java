package com.xpg.bookstore.bookstoremain.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.dao.BookDao;
import com.xpg.bookstore.bookstoremain.dao.UserDao;
import com.xpg.bookstore.bookstoremain.entity.Book;
import com.xpg.bookstore.bookstoremain.entity.CartItem;
import com.xpg.bookstore.bookstoremain.entity.User;
import com.xpg.bookstore.bookstoremain.service.CartService;
import com.xpg.bookstore.bookstoremain.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {
  @Autowired private BookDao bookDao;
  @Autowired private UserDao userDao;

  @Override
  public JSONArray getCart(long userId) {
    JSONArray res = new JSONArray();
    User user = userDao.findById(userId);
    if (user != null) for (CartItem item : user.getCart()) res.add(item.toJson());
    return res;
  }

  @Override
  public JSONObject addCart(long bookId, long userId) {
    Book book = bookDao.findById(bookId);
    User user = userDao.findById(userId);
    if (book == null) return Util.errorResponseJson("书籍不存在");
    if (user == null) return Util.errorResponseJson("用户不存在");
    if (user.getCart().stream().anyMatch(item -> item.getBook().getBookId() == bookId))
      return Util.errorResponseJson("书籍已在购物车中");
    user.getCart().add(new CartItem(user, book));
    userDao.save(user);
    return Util.successResponseJson("成功加入购物车");
  }

  @Override
  public JSONObject setNumber(long cartItemId, long userId, int number) {
    User user = userDao.findById(userId);
    if (user == null) return Util.errorResponseJson("用户不存在");
    if (number <= 0) return Util.errorResponseJson("数量不合法");
    CartItem item =
        user.getCart().stream()
            .filter(it -> it.getCartItemId() == cartItemId)
            .findFirst()
            .orElse(null);
    if (item == null) return Util.errorResponseJson("购物车项不存在");
    item.setNumber(number);
    userDao.save(user);
    return Util.successResponseJson("成功修改数量");
  }

  @Override
  public JSONObject delCart(long cartItemId, long userId) {
    User user = userDao.findById(userId);
    if (user == null) return Util.errorResponseJson("用户不存在");
    if (!user.getCart().removeIf(item -> item.getCartItemId() == cartItemId))
      return Util.errorResponseJson("购物车项不存在");
    userDao.save(user);
    return Util.successResponseJson("成功删除购物车项");
  }
}
