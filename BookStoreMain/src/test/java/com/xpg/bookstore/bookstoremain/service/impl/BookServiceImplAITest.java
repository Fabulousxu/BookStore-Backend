package com.xpg.bookstore.bookstoremain.service.impl;

import com.alibaba.fastjson2.JSONArray;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplAITest {

  @Mock private BookDao bookDao;

  @Mock private UserDao userDao;

  @Mock private CommentDao commentDao;

  @Mock private CategoryDao categoryDao;

  @InjectMocks private BookServiceImpl bookService;

  private Book book;
  private User user;
  private Comment comment;
  private Category category;
  private Category rootCategory;
  private Category categoryA;
  private Category categoryA1;

  @BeforeEach
  void setUp() {
    book = new Book();
    book.setBookId(1L);
    book.setTitle("Effective Java");
    book.setAuthor("Joshua Bloch");
    book.setCategoryCode(new HashSet<>(Collections.singletonList("root")));
    book.setComments(new ArrayList<>());

    user = new User();
    user.setUserId(1L);
    user.setUsername("testUser");

    comment = new Comment();
    comment.setCommentId(1L);
    comment.setContent("Great book!");
    comment.setUser(user);
    comment.setBook(book);
    comment.setCreatedAt(new Date());

    category = new Category();
    category.setCode("programming");
    category.setName("Programming");

    rootCategory = new Category();
    rootCategory.setCode("root");

    categoryA = new Category();
    categoryA.setCode("A");

    categoryA1 = new Category();
    categoryA1.setCode("A1");

    rootCategory.setSubCategories(Collections.singletonList(categoryA));
    categoryA.setSubCategories(Collections.singletonList(categoryA1));
    categoryA1.setSubCategories(Collections.emptyList());
  }

  @ParameterizedTest
  @CsvSource({"Java, 0, 10", "Spring, 1, 5", "Hibernate, 2, 20"})
  void searchBooks(String keyword, int pageIndex, int pageSize) {
    Page<Book> bookPage = new PageImpl<>(Collections.singletonList(book));
    when(bookDao.findByKeyword(keyword, PageRequest.of(pageIndex, pageSize))).thenReturn(bookPage);

    JSONObject result = bookService.searchBooks(keyword, pageIndex, pageSize);

    assertNotNull(result);
    assertEquals(1, result.getIntValue("totalNumber"));
    assertEquals(1, result.getIntValue("totalPage"));
    JSONArray items = result.getJSONArray("items");
    assertEquals(1, items.size());
    assertEquals(book.getBookId(), items.getJSONObject(0).getLong("id"));
  }

  @Test
  void searchBooksByCategory_CategoryNull() {
    // 模拟行为
    Page<Book> bookPage = new PageImpl<>(Collections.singletonList(book));
    when(bookDao.findByKeyword("", PageRequest.of(0, 10))).thenReturn(bookPage);

    // 调用方法
    JSONObject result = bookService.searchBooksByCategory(null, 0, 10);

    // 验证结果
    assertNotNull(result);
    assertEquals(1, result.getIntValue("totalNumber"));
    assertEquals(1, result.getIntValue("totalPage"));
    assertEquals(1, result.getJSONArray("items").size());
  }

  @Test
  void searchBooksByCategory_CategoryRoot() {
    // 模拟行为
    Page<Book> bookPage = new PageImpl<>(Collections.singletonList(book));
    when(bookDao.findByKeyword("", PageRequest.of(0, 10))).thenReturn(bookPage);

    // 调用方法
    JSONObject result = bookService.searchBooksByCategory("root", 0, 10);

    // 验证结果
    assertNotNull(result);
    assertEquals(1, result.getIntValue("totalNumber"));
    assertEquals(1, result.getIntValue("totalPage"));
    assertEquals(1, result.getJSONArray("items").size());
  }

  @Test
  void searchBooksByCategory_CategoryNotFound() {
    // 模拟行为
    when(categoryDao.findByCode("invalidCode")).thenReturn(null);

    // 调用方法
    JSONObject result = bookService.searchBooksByCategory("invalidCode", 0, 10);

    // 验证结果
    assertNotNull(result);
    assertEquals(0, result.getIntValue("totalNumber"));
    assertEquals(0, result.getIntValue("totalPage"));
    assertEquals(0, result.getJSONArray("items").size());
  }

  @Test
  void searchBooksByCategory_ValidCategoryWithSubCategories() {
    // 模拟行为
    when(categoryDao.findByCode("A")).thenReturn(categoryA);
    Set<String> categoryCodeSet = new HashSet<>(Arrays.asList("A", "A1"));
    Page<Book> bookPage = new PageImpl<>(Collections.singletonList(book));
    when(bookDao.findByCategoryCodeContains(categoryCodeSet, PageRequest.of(0, 10)))
        .thenReturn(bookPage);

    // 调用方法
    JSONObject result = bookService.searchBooksByCategory("A", 0, 10);

    // 验证结果
    assertNotNull(result);
    assertEquals(1, result.getIntValue("totalNumber"));
    assertEquals(1, result.getIntValue("totalPage"));
    assertEquals(1, result.getJSONArray("items").size());
  }

  @Test
  void searchBooksByCategory_ValidCategoryWithParent() {
    // 模拟行为
    when(categoryDao.findByCode("A1")).thenReturn(categoryA1);
    when(categoryDao.findByCode("A")).thenReturn(categoryA);
    Set<String> categoryCodeSet = new HashSet<>(Arrays.asList("A1", "A"));
    Page<Book> bookPage = new PageImpl<>(Collections.singletonList(book));
    when(bookDao.findByCategoryCodeContains(categoryCodeSet, PageRequest.of(0, 10)))
        .thenReturn(bookPage);

    // 调用方法
    JSONObject result = bookService.searchBooksByCategory("A1", 0, 10);

    // 验证结果
    assertNotNull(result);
    assertEquals(1, result.getIntValue("totalNumber"));
    assertEquals(1, result.getIntValue("totalPage"));
    assertEquals(1, result.getJSONArray("items").size());
  }

  @ParameterizedTest
  @CsvSource({"Effective, 0, 10", "Java, 1, 5", "Spring, 2, 20"})
  void searchBooksByTitle(String title, int pageIndex, int pageSize) {
    Page<Book> bookPage = new PageImpl<>(Collections.singletonList(book));
    when(bookDao.findByTitleContains(title, PageRequest.of(pageIndex, pageSize)))
        .thenReturn(bookPage);

    JSONObject result = bookService.searchBooksByTitle(title, pageIndex, pageSize);

    assertNotNull(result);
    assertEquals(1, result.getIntValue("totalNumber"));
    assertEquals(1, result.getIntValue("totalPage"));
    JSONArray items = result.getJSONArray("items");
    assertEquals(1, items.size());
    assertEquals(book.getBookId(), items.getJSONObject(0).getLong("id"));
  }

  @Test
  void getBookInfo() {
    when(bookDao.findById(1L)).thenReturn(book);

    JSONObject result = bookService.getBookInfo(1L);

    assertNotNull(result);
    assertEquals(book.getTitle(), result.getString("title"));
    assertEquals(book.getAuthor(), result.getString("author"));
  }

  @ParameterizedTest
  @CsvSource({"1, 0, 10, latest, 1", "2, 1, 5, oldest, 2"})
  void getBookComments(long bookId, int pageIndex, int pageSize, String sort, long userId) {
    Page<Comment> commentPage = new PageImpl<>(Collections.singletonList(comment));
    when(commentDao.findByBookId(bookId, PageRequest.of(pageIndex, pageSize)))
        .thenReturn(commentPage);
    when(commentDao.addMessageToJson(comment, userId)).thenReturn(JSONObject.from(comment));

    JSONObject result = bookService.getBookComments(bookId, pageIndex, pageSize, sort, userId);

    assertNotNull(result);
    assertEquals(1, result.getIntValue("totalNumber"));
    assertEquals(1, result.getIntValue("totalPage"));
    JSONArray items = result.getJSONArray("items");
    assertEquals(1, items.size());
    assertEquals(comment.getCommentId(), items.getJSONObject(0).getLong("id"));
  }

  @ParameterizedTest
  @CsvSource({"1, 1, Great book!", "2, 2, Awesome!"})
  void postComment(long bookId, long userId, String content) {
    when(bookDao.findById(bookId)).thenReturn(book);
    when(userDao.findById(userId)).thenReturn(user);

    JSONObject result = bookService.postComment(bookId, userId, content);

    assertNotNull(result);
    assertTrue(result.getBoolean("ok"));
    assertEquals("评论成功", result.getString("message"));
    verify(bookDao, times(1)).save(book);
  }

  @ParameterizedTest
  @CsvSource({
    "1, Effective Java, Joshua Bloch, 978-0134685991, cover.jpg, Description, 500, 100, 50",
    "2, Clean Code, Robert C. Martin, 978-0132350884, cover2.jpg, Description 2, 600, 200, 100"
  })
  void setBookInfo(
      long bookId,
      String title,
      String author,
      String isbn,
      String cover,
      String description,
      int price,
      int sales,
      int repertory) {
    when(bookDao.findById(bookId)).thenReturn(book);

    JSONObject result =
        bookService.setBookInfo(
            bookId, title, author, isbn, cover, description, price, sales, repertory);

    assertNotNull(result);
    assertTrue(result.getBoolean("ok"));
    assertEquals("修改成功", result.getString("message"));
    verify(bookDao, times(1)).save(book);
  }

  @ParameterizedTest
  @CsvSource({
    "Effective Java, Joshua Bloch, 978-0134685991, cover.jpg, Description, 500, 100, 50",
    "Clean Code, Robert C. Martin, 978-0132350884, cover2.jpg, Description 2, 600, 200, 100"
  })
  void addBook(
      String title,
      String author,
      String isbn,
      String cover,
      String description,
      int price,
      int sales,
      int repertory) {
    JSONObject result =
        bookService.addBook(title, author, isbn, cover, description, price, sales, repertory);

    assertNotNull(result);
    assertTrue(result.getBoolean("ok"));
    assertEquals("添加成功", result.getString("message"));
    verify(bookDao, times(1)).save(any(Book.class));
  }

  @ParameterizedTest
  @CsvSource({"1", "2"})
  void deleteBook(long bookId) {
    when(bookDao.existsById(bookId)).thenReturn(true);

    JSONObject result = bookService.deleteBook(bookId);

    assertNotNull(result);
    assertTrue(result.getBoolean("ok"));
    assertEquals("删除成功", result.getString("message"));
    verify(bookDao, times(1)).deleteById(bookId);
  }
}
