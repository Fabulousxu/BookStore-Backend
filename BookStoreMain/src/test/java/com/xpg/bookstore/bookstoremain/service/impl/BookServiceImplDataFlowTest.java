package com.xpg.bookstore.bookstoremain.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.dao.BookDao;
import com.xpg.bookstore.bookstoremain.dao.CategoryDao;
import com.xpg.bookstore.bookstoremain.dao.CommentDao;
import com.xpg.bookstore.bookstoremain.dao.UserDao;
import com.xpg.bookstore.bookstoremain.entity.Book;
import com.xpg.bookstore.bookstoremain.entity.Category;
import com.xpg.bookstore.bookstoremain.entity.Comment;
import com.xpg.bookstore.bookstoremain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BookServiceImplDataFlowTest {

  @Mock private BookDao bookDao;
  @Mock private CategoryDao categoryDao;
  @Mock private CommentDao commentDao;
  @Mock private UserDao userDao;

  @InjectMocks private BookServiceImpl bookService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  // 测试变量定义-使用路径
  @Test
  void searchBooks_ShouldCorrectlyUseKeywordVariable() {
    // 定义变量
    String keyword = "test";
    int pageIndex = 0;
    int pageSize = 10;

    Book book1 = new Book();
    book1.setTitle("Test Book 1");
    Book book2 = new Book();
    book2.setTitle("Test Book 2");
    Page<Book> page = new PageImpl<>(Arrays.asList(book1, book2));

    // 模拟变量使用
    when(bookDao.findByKeyword(keyword, PageRequest.of(pageIndex, pageSize))).thenReturn(page);

    // 验证变量流动
    JSONObject result = bookService.searchBooks(keyword, pageIndex, pageSize);

    assertEquals(2, result.getJSONArray("items").size());
    assertEquals(1, result.getIntValue("totalPage"));
    verify(bookDao).findByKeyword(keyword, PageRequest.of(pageIndex, pageSize));
  }

  @Test
  void searchBooksByCategory_ShouldHandleCategoryVariableFlow() {
    // 定义变量
    String categoryCode = "A1";
    Category category = new Category();
    category.setCode(categoryCode);
    category.setSubCategories(new ArrayList<>());
    // category.setParentCode("parent");

    Category parent = new Category();
    parent.setCode("A");
    parent.setSubCategories(List.of(category));
    // parent.setParentCode("root");
    parent.setSubCategories(Collections.singletonList(category));

    // 模拟变量流动路径
    when(categoryDao.findByCode(categoryCode)).thenReturn(category);
    when(categoryDao.findByCode("A")).thenReturn(parent);

    Book book = new Book();
    book.setTitle("Category Book");
    Page<Book> page = new PageImpl<>(Collections.singletonList(book));
    when(bookDao.findByCategoryCodeContains(anySet(), any())).thenReturn(page);

    // 验证变量使用
    JSONObject result = bookService.searchBooksByCategory(categoryCode, 0, 10);

    assertEquals(1, result.getJSONArray("items").size());
    assertTrue(result.getIntValue("totalNumber") > 0);
  }

  @Test
  void getBookInfo_ShouldHandleBookIdVariableFlow() {
    // 定义变量
    long bookId = 1L;
    Book book = new Book();
    book.setBookId(bookId);
    book.setTitle("Existing Book");

    // 模拟变量流动
    when(bookDao.findById(bookId)).thenReturn(book);

    // 验证变量使用
    JSONObject result = bookService.getBookInfo(bookId);

    assertNotNull(result);
    assertEquals("Existing Book", result.getString("title"));
  }

  @Test
  void postComment_ShouldVerifyDataFlowForAllVariables() {
    // 定义所有相关变量
    long bookId = 1L;
    long userId = 1L;
    String content = "Test comment content";

    Book book = new Book();
    book.setComments(new ArrayList<>());
    User user = new User();
    Comment comment = new Comment();

    // 模拟变量流动路径
    when(bookDao.findById(bookId)).thenReturn(book);
    when(userDao.findById(userId)).thenReturn(user);
    when(bookDao.save(any())).thenReturn(book);

    // 验证变量使用
    JSONObject result = bookService.postComment(bookId, userId, content);

    assertEquals("评论成功", result.getString("message"));
    verify(bookDao).findById(bookId);
    verify(userDao).findById(userId);
    verify(bookDao).save(book);
  }

  @Test
  void setBookInfo_ShouldCoverAllVariablePaths() {
    // 定义所有变量
    long bookId = 1L;
    String title = "New Title";
    String author = "New Author";
    String isbn = "123456";
    String cover = "cover.jpg";
    String description = "New Description";
    int price = 100;
    int sales = 50;
    int repertory = 200;

    Book book = new Book();

    // 模拟变量流动
    when(bookDao.findById(bookId)).thenReturn(book);
    when(bookDao.save(any())).thenReturn(book);

    // 验证变量使用
    JSONObject result =
        bookService.setBookInfo(
            bookId, title, author, isbn, cover, description, price, sales, repertory);

    assertEquals("修改成功", result.getString("message"));
    verify(bookDao).findById(bookId);
    verify(bookDao).save(book);
  }

  @Test
  void addBook_ShouldVerifyAllInputVariablesAreUsed() {
    // 定义所有输入变量
    String title = "New Book";
    String author = "Author";
    String isbn = "123456";
    String cover = "cover.jpg";
    String description = "Description";
    int price = 100;
    int sales = 0;
    int repertory = 10;

    // 模拟变量使用
    when(bookDao.save(any())).thenReturn(new Book());

    // 验证变量流动
    JSONObject result =
        bookService.addBook(title, author, isbn, cover, description, price, sales, repertory);

    assertEquals("添加成功", result.getString("message"));
    verify(bookDao).save(any(Book.class));
  }

  @Test
  void deleteBook_ShouldVerifyBookIdVariableFlow() {
    // 定义变量
    long existingBookId = 1L;
    long nonExistingBookId = 999L;

    // 模拟变量流动路径
    when(bookDao.existsById(existingBookId)).thenReturn(true);
    when(bookDao.existsById(nonExistingBookId)).thenReturn(false);

    // 验证变量使用
    JSONObject successResult = bookService.deleteBook(existingBookId);
    JSONObject failResult = bookService.deleteBook(nonExistingBookId);

    assertEquals("删除成功", successResult.getString("message"));
    assertEquals("书籍不存在", failResult.getString("message"));
    verify(bookDao).deleteById(existingBookId);
  }

  @Test
  void getBookComments_ShouldCoverAllVariablePaths() {
    // 定义变量
    long bookId = 1L;
    int pageIndex = 0;
    int pageSize = 10;
    String sort = "date";
    long userId = 1L;

    Comment comment = new Comment();
    User user = new User();
    user.setUserId(userId);
    comment.setUser(user);

    Page<Comment> page = new PageImpl<>(Collections.singletonList(comment));

    // 模拟变量流动
    when(commentDao.findByBookId(bookId, PageRequest.of(pageIndex, pageSize))).thenReturn(page);
    when(commentDao.addMessageToJson(any(), eq(userId))).thenReturn(new JSONObject());

    // 验证变量使用
    JSONObject result = bookService.getBookComments(bookId, pageIndex, pageSize, sort, userId);

    assertEquals(1, result.getJSONArray("items").size());
    verify(commentDao).findByBookId(bookId, PageRequest.of(pageIndex, pageSize));
  }

  @Test
  void searchBooksByTitle_ShouldVerifyTitleVariableFlow() {
    // 定义变量
    String title = "Java";
    int pageIndex = 0;
    int pageSize = 10;

    Book book = new Book();
    book.setTitle("Java Programming");
    Page<Book> page = new PageImpl<>(Collections.singletonList(book));

    // 模拟变量使用
    when(bookDao.findByTitleContains(title, PageRequest.of(pageIndex, pageSize))).thenReturn(page);

    // 验证变量流动
    JSONObject result = bookService.searchBooksByTitle(title, pageIndex, pageSize);

    assertEquals(1, result.getJSONArray("items").size());
    verify(bookDao).findByTitleContains(title, PageRequest.of(pageIndex, pageSize));
  }

  @Test
  void searchBooksByCategory_ShouldHandleRootCategoryVariable() {
    // 定义变量
    String rootCategory = "root";
    int pageIndex = 0;
    int pageSize = 10;

    Book book1 = new Book();
    book1.setTitle("Book 1");
    Book book2 = new Book();
    book2.setTitle("Book 2");
    Page<Book> page = new PageImpl<>(Arrays.asList(book1, book2));

    // 模拟变量流动
    when(bookDao.findByKeyword("", PageRequest.of(pageIndex, pageSize))).thenReturn(page);

    // 验证变量使用
    JSONObject result = bookService.searchBooksByCategory(rootCategory, pageIndex, pageSize);

    assertEquals(2, result.getJSONArray("items").size());
    verify(bookDao).findByKeyword("", PageRequest.of(pageIndex, pageSize));
  }
}
