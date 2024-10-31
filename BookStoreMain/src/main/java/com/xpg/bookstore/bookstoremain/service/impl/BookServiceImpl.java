package com.xpg.bookstore.bookstoremain.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.entity.User;
import com.xpg.bookstore.bookstoremain.dao.BookDao;
import com.xpg.bookstore.bookstoremain.dao.CommentDao;
import com.xpg.bookstore.bookstoremain.dao.UserDao;
import com.xpg.bookstore.bookstoremain.entity.Book;
import com.xpg.bookstore.bookstoremain.entity.Comment;
import com.xpg.bookstore.bookstoremain.service.BookService;
import com.xpg.bookstore.bookstoremain.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {
  @Autowired private BookDao bookDao;
  @Autowired private UserDao userDao;
  @Autowired private CommentDao commentDao;

  @Override
  public JSONObject searchBooks(String keyword, int pageIndex, int pageSize) {
    JSONObject res = new JSONObject();
    Page<Book> bookPage = bookDao.findByKeyword(keyword, PageRequest.of(pageIndex, pageSize));
    res.put("totalNumber", bookPage.getTotalElements());
    res.put("totalPage", bookPage.getTotalPages());
    JSONArray items = new JSONArray();
    for (Book book : bookPage) items.add(book.toJson());
    res.put("items", items);
    return res;
  }

  @Override
  public JSONObject getBookInfo(long bookId) {
    Book book = bookDao.findById(bookId);
    return book == null ? null : book.toJson();
  }

  @Override
  public JSONObject getBookComments(
      long bookId, int pageIndex, int pageSize, String sort, long userId) {
    JSONObject res = new JSONObject();
    Page<Comment> commentPage =
        commentDao.findByBookId(bookId, PageRequest.of(pageIndex, pageSize));
    res.put("totalNumber", commentPage.getTotalElements());
    res.put("totalPage", commentPage.getTotalPages());
    JSONArray items = new JSONArray();
    for (Comment comment : commentPage) items.add(comment.toJson(userDao.findById(userId)));
    res.put("items", items);
    return res;
  }

  @Override
  public JSONObject postComment(long bookId, long userId, String content) {
    Book book = bookDao.findById(bookId);
    User user = userDao.findById(userId);
    if (book == null) return Util.errorResponseJson("书籍不存在");
    if (user == null) return Util.errorResponseJson("用户不存在");
    book.getComments().add(new Comment(user, book, content));
    bookDao.save(book);
    return Util.successResponseJson("评论成功");
  }

  @Override
  public JSONObject setBookInfo(
      long bookId,
      String title,
      String author,
      String isbn,
      String cover,
      String description,
      int price,
      int sales,
      int repertory) {
    Book book = bookDao.findById(bookId);
    if (book == null) return Util.errorResponseJson("书籍不存在");
    book.setTitle(title);
    book.setAuthor(author);
    book.setIsbn(isbn);
    book.setCover(cover);
    book.setDescription(description);
    book.setPrice(price);
    book.setSales(sales);
    book.setRepertory(repertory);
    bookDao.save(book);
    return Util.successResponseJson("修改成功");
  }

  @Override
  public JSONObject addBook(
      String title,
      String author,
      String isbn,
      String cover,
      String description,
      int price,
      int sales,
      int repertory) {
    bookDao.save(new Book(title, author, isbn, cover, description, price, sales, repertory));
    return Util.successResponseJson("添加成功");
  }

  @Override
  public JSONObject deleteBook(long bookId) {
    Book book = bookDao.findById(bookId);
    if (book == null) return Util.errorResponseJson("书籍不存在");
    bookDao.delete(book);
    return Util.successResponseJson("删除成功");
  }
}
