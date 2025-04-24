package com.xpg.bookstore.bookstoremain.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.dao.BookDao;
import com.xpg.bookstore.bookstoremain.dao.UserDao;
import com.xpg.bookstore.bookstoremain.entity.Book;
import com.xpg.bookstore.bookstoremain.entity.CartItem;
import com.xpg.bookstore.bookstoremain.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CartServiceImplDUPathTest {
  @InjectMocks private CartServiceImpl cartService;
  @Mock private BookDao bookDao;
  @Mock private UserDao userDao;

  @Test
  void getCart() {
    CartItem cartItem = new CartItem();
    cartItem.setBook(new Book());
    User user1 = new User();
    user1.setCart(List.of(cartItem));
    User user2 = new User();
    user2.setCart(List.of());
    when(userDao.findById(eq(1L))).thenReturn(user1);
    when(userDao.findById(eq(2L))).thenReturn(user2);
    when(userDao.findById(eq(3L))).thenReturn(null);

    JSONArray noEmptyResult = cartService.getCart(1L);
    assertEquals(1, noEmptyResult.size());
    JSONArray emptyResult = cartService.getCart(2L);
    assertEquals(0, emptyResult.size());
    JSONArray noneUserResult = cartService.getCart(3L);
    assertEquals(0, noneUserResult.size());
  }

  @Test
  void addCart() {
    Book book1 = new Book();
    book1.setBookId(1);
    Book book2 = new Book();
    book2.setBookId(2);
    User user = new User();
    CartItem cartItem = new CartItem();
    cartItem.setBook(book1);
    List<CartItem> cartItems = new ArrayList<>();
    cartItems.add(cartItem);
    user.setCart(cartItems);
    when(userDao.findById(1L)).thenReturn(user);
    when(userDao.findById(2L)).thenReturn(null);
    when(bookDao.findById(1L)).thenReturn(book1);
    when(bookDao.findById(2L)).thenReturn(book2);
    when(bookDao.findById(3L)).thenReturn(null);
    when(userDao.save(any(User.class))).thenReturn(user);

    JSONObject noneBookResult = cartService.addCart(3L, 1L);
    assertFalse(noneBookResult.getBoolean("ok"));
    JSONObject noneUserResult = cartService.addCart(1L, 2L);
    assertFalse(noneUserResult.getBoolean("ok"));
    JSONObject existedBookResult = cartService.addCart(1L, 1L);
    assertFalse(existedBookResult.getBoolean("ok"));
    JSONObject successResult = cartService.addCart(2L, 1L);
    assertTrue(successResult.getBoolean("ok"));
  }

  @Test
  void setNumber() {
    User user = new User();
    CartItem cartItem = new CartItem();
    cartItem.setCartItemId(1L);
    List<CartItem> cartItems = new ArrayList<>();
    cartItems.add(cartItem);
    user.setCart(cartItems);
    when(userDao.findById(1L)).thenReturn(user);
    when(userDao.findById(2L)).thenReturn(null);

    JSONObject noneUserResult = cartService.setNumber(1, 2, 1);
    assertFalse(noneUserResult.getBoolean("ok"));
    JSONObject invalidNumberResult = cartService.setNumber(1, 1, -1);
    assertFalse(invalidNumberResult.getBoolean("ok"));
    JSONObject noneCartResult = cartService.setNumber(2, 1, 1);
    assertFalse(noneCartResult.getBoolean("ok"));
    JSONObject successResult = cartService.setNumber(1, 1, 1);
    assertTrue(successResult.getBoolean("ok"));
  }

  @Test
  void delCart() {
    User user = new User();
    CartItem cartItem = new CartItem();
    cartItem.setCartItemId(1L);
    List<CartItem> cartItems = new ArrayList<>();
    cartItems.add(cartItem);
    user.setCart(cartItems);
    when(userDao.findById(1L)).thenReturn(user);
    when(userDao.findById(2L)).thenReturn(null);

    JSONObject noneUserResult = cartService.delCart(1, 2);
    assertFalse(noneUserResult.getBoolean("ok"));
    JSONObject noneCartResult = cartService.delCart(2, 1);
    assertFalse(noneCartResult.getBoolean("ok"));
    JSONObject successResult = cartService.delCart(1, 1);
    assertTrue(successResult.getBoolean("ok"));
  }
}
