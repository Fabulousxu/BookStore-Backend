package com.xpg.bookstore.bookstoremain.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.dao.BookDao;
import com.xpg.bookstore.bookstoremain.dao.CategoryDao;
import com.xpg.bookstore.bookstoremain.dao.CommentDao;
import com.xpg.bookstore.bookstoremain.dao.UserDao;
import com.xpg.bookstore.bookstoremain.entity.Book;
import com.xpg.bookstore.bookstoremain.entity.Category;
import com.xpg.bookstore.bookstoremain.entity.Comment;
import com.xpg.bookstore.bookstoremain.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplDUPathTest {
  @InjectMocks private BookServiceImpl bookService;
  @Mock private BookDao bookDao;
  @Mock private CategoryDao categoryDao;
  @Mock private CommentDao commentDao;
  @Mock private UserDao userDao;

  @Test
  void searchBooks() {
    Book javaBook = new Book();
    Book pythonBook = new Book();
    List<Book> javaList = List.of(javaBook);
    Page<Book> javaPage = new PageImpl<>(javaList, PageRequest.of(0, 10), 1);
    when(bookDao.findByKeyword(eq("Java"), any(Pageable.class))).thenReturn(javaPage);
    List<Book> defaultList = List.of(javaBook, pythonBook);
    Page<Book> defaultPage = new PageImpl<>(defaultList, PageRequest.of(0, 10), 2);
    when(bookDao.findByKeyword(eq(""), any(Pageable.class))).thenReturn(defaultPage);
    List<Book> noneList = List.of();
    Page<Book> nonePage = new PageImpl<>(noneList, PageRequest.of(0, 10), 0);
    when(bookDao.findByKeyword(eq("NotExistsBook"), any(Pageable.class))).thenReturn(nonePage);

    JSONObject javaResult = bookService.searchBooks("Java", 0, 10);
    assertEquals(1, javaResult.getInteger("totalNumber"));
    assertEquals(1, javaResult.getInteger("totalPage"));
    JSONObject defaultResult = bookService.searchBooks("", 0, 10);
    assertEquals(2, defaultResult.getInteger("totalNumber"));
    assertEquals(1, defaultResult.getInteger("totalPage"));
    JSONObject noneResult = bookService.searchBooks("NotExistsBook", 0, 10);
    assertEquals(0, noneResult.getInteger("totalNumber"));
    assertEquals(0, noneResult.getInteger("totalPage"));
  }

  @Test
  void searchBooksByCategory() {
    Category rootCategory = new Category();
    rootCategory.setCode("root");
    Category xCategory = new Category();
    xCategory.setCode("x");
    Category yCategory = new Category();
    yCategory.setCode("y");
    Category x1Category = new Category();
    x1Category.setCode("x1");
    Category x2Category = new Category();
    x2Category.setCode("x2");
    Category x11Category = new Category();
    x11Category.setCode("x11");
    Category x12Category = new Category();
    x12Category.setCode("x12");
    Category x21Category = new Category();
    x21Category.setCode("x21");
    Category x22Category = new Category();
    x22Category.setCode("x22");
    Category x111Category = new Category();
    x111Category.setCode("x111");
    Category x112Category = new Category();
    x112Category.setCode("x112");
    Category x121Category = new Category();
    x121Category.setCode("x121");
    Category x122Category = new Category();
    x122Category.setCode("x122");

    rootCategory.setSubCategories(List.of(xCategory, yCategory));
    xCategory.setSubCategories(List.of(x1Category, x2Category));
    yCategory.setSubCategories(List.of());
    x1Category.setSubCategories(List.of(x11Category, x12Category));
    x2Category.setSubCategories(List.of(x21Category, x22Category));
    x11Category.setSubCategories(List.of(x111Category, x112Category));
    x12Category.setSubCategories(List.of(x121Category, x122Category));
    x21Category.setSubCategories(List.of());
    x22Category.setSubCategories(List.of());
    x111Category.setSubCategories(List.of());
    x112Category.setSubCategories(List.of());
    x121Category.setSubCategories(List.of());
    x122Category.setSubCategories(List.of());

    Book xBook = new Book();
    Book yBook = new Book();
    Book x1Book = new Book();
    Book x2Book = new Book();
    Book x11Book = new Book();
    Book x12Book = new Book();
    Book x21Book = new Book();
    Book x22Book = new Book();
    Book x111Book = new Book();
    Book x112Book = new Book();
    Book x121Book = new Book();
    Book x122Book = new Book();

    when(categoryDao.findByCode(eq("x"))).thenReturn(xCategory);
    when(categoryDao.findByCode(eq("x1"))).thenReturn(x1Category);
    when(categoryDao.findByCode(eq("x11"))).thenReturn(x11Category);
    when(categoryDao.findByCode(eq("x123"))).thenReturn(null);
    List<Book> defaultList =
        List.of(
            xBook, yBook, x1Book, x2Book, x11Book, x12Book, x21Book, x22Book, x111Book, x112Book,
            x121Book, x122Book);
    Page<Book> defaultPage = new PageImpl<>(defaultList, PageRequest.of(0, 10), 12);
    when(bookDao.findByKeyword(eq(""), any(Pageable.class))).thenReturn(defaultPage);
    Set<String> xSet = Set.of("x", "x1", "x2", "x11", "x12", "x21", "x22");
    List<Book> xList = List.of(xBook, x1Book, x2Book, x11Book, x12Book, x21Book, x22Book);
    Page<Book> xPage = new PageImpl<>(xList, PageRequest.of(0, 10), 7);
    when(bookDao.findByCategoryCodeContains(eq(xSet), any(Pageable.class))).thenReturn(xPage);
    Set<String> x1Set = Set.of("x1", "x", "x2", "x11", "x12", "x111", "x112", "x121", "x122");
    List<Book> x1List =
        List.of(x1Book, xBook, x2Book, x11Book, x12Book, x111Book, x112Book, x121Book, x122Book);
    Page<Book> x1Page = new PageImpl<>(x1List, PageRequest.of(0, 10), 9);
    when(bookDao.findByCategoryCodeContains(eq(x1Set), any(Pageable.class))).thenReturn(x1Page);
    Set<String> x11Set = Set.of("x11", "x1", "x12", "x", "x111", "x112");
    List<Book> x11List = List.of(x11Book, x1Book, x12Book, xBook, x111Book, x112Book);
    Page<Book> x11Page = new PageImpl<>(x11List, PageRequest.of(0, 10), 6);
    when(bookDao.findByCategoryCodeContains(eq(x11Set), any(Pageable.class))).thenReturn(x11Page);

    JSONObject rootResult = bookService.searchBooksByCategory("root", 0, 10);
    assertEquals(12, rootResult.getInteger("totalNumber"));
    assertEquals(2, rootResult.getInteger("totalPage"));
    JSONObject defaultResult = bookService.searchBooksByCategory("", 0, 10);
    assertEquals(12, defaultResult.getInteger("totalNumber"));
    assertEquals(2, defaultResult.getInteger("totalPage"));
    JSONObject nullResult = bookService.searchBooksByCategory(null, 0, 10);
    assertEquals(12, nullResult.getInteger("totalNumber"));
    assertEquals(2, nullResult.getInteger("totalPage"));
    JSONObject noneResult = bookService.searchBooksByCategory("x123", 0, 10);
    assertEquals(0, noneResult.getInteger("totalNumber"));
    assertEquals(0, noneResult.getInteger("totalPage"));
    JSONObject xResult = bookService.searchBooksByCategory("x", 0, 10);
    assertEquals(7, xResult.getInteger("totalNumber"));
    assertEquals(1, xResult.getInteger("totalPage"));
    JSONObject x1Result = bookService.searchBooksByCategory("x1", 0, 10);
    assertEquals(9, x1Result.getInteger("totalNumber"));
    assertEquals(1, x1Result.getInteger("totalPage"));
    JSONObject x11Result = bookService.searchBooksByCategory("x11", 0, 10);
    assertEquals(6, x11Result.getInteger("totalNumber"));
    assertEquals(1, x11Result.getInteger("totalPage"));
  }

  @Test
  void searchBooksByTitle() {
    Book javaBook = new Book();
    Book pythonBook = new Book();
    List<Book> javaList = List.of(javaBook);
    Page<Book> javaPage = new PageImpl<>(javaList, PageRequest.of(0, 10), 1);
    when(bookDao.findByTitleContains(eq("Java"), any(Pageable.class))).thenReturn(javaPage);
    List<Book> defaultList = List.of(javaBook, pythonBook);
    Page<Book> defaultPage = new PageImpl<>(defaultList, PageRequest.of(0, 10), 2);
    when(bookDao.findByTitleContains(eq(""), any(Pageable.class))).thenReturn(defaultPage);
    List<Book> noneList = List.of();
    Page<Book> nonePage = new PageImpl<>(noneList, PageRequest.of(0, 10), 0);
    when(bookDao.findByTitleContains(eq("NotExistsBook"), any(Pageable.class)))
        .thenReturn(nonePage);

    JSONObject javaResult = bookService.searchBooksByTitle("Java", 0, 10);
    assertEquals(1, javaResult.getInteger("totalNumber"));
    assertEquals(1, javaResult.getInteger("totalPage"));
    JSONObject defaultResult = bookService.searchBooksByTitle("", 0, 10);
    assertEquals(2, defaultResult.getInteger("totalNumber"));
    assertEquals(1, defaultResult.getInteger("totalPage"));
    JSONObject noneResult = bookService.searchBooksByTitle("NotExistsBook", 0, 10);
    assertEquals(0, noneResult.getInteger("totalNumber"));
    assertEquals(0, noneResult.getInteger("totalPage"));
  }

  @Test
  void getBookInfo() {
    Book book = new Book();
    book.setBookId(1);
    book.setTitle("Java");
    when(bookDao.findById(eq(1L))).thenReturn(book);
    when(bookDao.findById(eq(2L))).thenReturn(null);

    JSONObject bookResult = bookService.getBookInfo(1);
    assertEquals(1, bookResult.getInteger("id"));
    assertEquals("Java", bookResult.getString("title"));
    JSONObject noneResult = bookService.getBookInfo(2);
    assertNull(noneResult);
  }

  @Test
  void getBookComments() {
    Comment comment = new Comment();
    List<Comment> commentList = List.of(comment);
    Page<Comment> commentPage = new PageImpl<>(commentList, PageRequest.of(0, 10), 1);
    when(commentDao.findByBookId(eq(1L), any(Pageable.class))).thenReturn(commentPage);
    List<Comment> noneList = List.of();
    Page<Comment> nonePage = new PageImpl<>(noneList, PageRequest.of(0, 10), 0);
    when(commentDao.findByBookId(eq(2L), any(Pageable.class))).thenReturn(nonePage);
    when(commentDao.addMessageToJson(any(Comment.class), any(long.class)))
        .thenReturn(new JSONObject());

    JSONObject commentResult = bookService.getBookComments(1, 0, 10, "", 0);
    assertEquals(1, commentResult.getInteger("totalNumber"));
    assertEquals(1, commentResult.getInteger("totalPage"));
    JSONObject noneResult = bookService.getBookComments(2, 0, 10, "", 0);
    assertEquals(0, noneResult.getInteger("totalNumber"));
    assertEquals(0, noneResult.getInteger("totalPage"));
  }

  @Test
  void postComment() {
    Book book = new Book();
    book.setComments(new ArrayList<>());
    User user = new User();
    when(bookDao.findById(eq(1L))).thenReturn(book);
    when(bookDao.findById(eq(2L))).thenReturn(null);
    when(userDao.findById(eq(1L))).thenReturn(user);
    when(userDao.findById(eq(2L))).thenReturn(null);
    when(bookDao.save(any(Book.class))).thenReturn(book);

    JSONObject successResult = bookService.postComment(1, 1, "");
    assertTrue(successResult.getBoolean("ok"));
    JSONObject noneBookResult = bookService.postComment(2, 1, "");
    assertFalse(noneBookResult.getBoolean("ok"));
    JSONObject noneUserResult = bookService.postComment(1, 2, "");
    assertFalse(noneUserResult.getBoolean("ok"));
  }

  @Test
  void setBookInfo() {
    Book book = new Book();
    when(bookDao.findById(eq(1L))).thenReturn(book);
    when(bookDao.findById(eq(2L))).thenReturn(null);
    when(bookDao.save(any(Book.class))).thenReturn(book);

    JSONObject successResult = bookService.setBookInfo(1, "", "", "", "", "", 0, 0, 0);
    assertTrue(successResult.getBoolean("ok"));
    JSONObject noneBookResult = bookService.setBookInfo(2, "", "", "", "", "", 0, 0, 0);
    assertFalse(noneBookResult.getBoolean("ok"));
  }

  @Test
  void addBook() {
    when(bookDao.save(any(Book.class))).thenReturn(new Book());
    JSONObject result = bookService.addBook("", "", "", "", "", 0, 0, 0);
    assertTrue(result.getBoolean("ok"));
  }

  @Test
  void deleteBook() {
    when(bookDao.existsById(eq(1L))).thenReturn(true);
    when(bookDao.existsById(eq(2L))).thenReturn(false);

    JSONObject successResult = bookService.deleteBook(1);
    assertTrue(successResult.getBoolean("ok"));
    JSONObject noneResult = bookService.deleteBook(2);
    assertFalse(noneResult.getBoolean("ok"));
  }
}
