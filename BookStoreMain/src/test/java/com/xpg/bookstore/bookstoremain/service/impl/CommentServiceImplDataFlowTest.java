package com.xpg.bookstore.bookstoremain.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.dao.CommentDao;
import com.xpg.bookstore.bookstoremain.dao.UserDao;
import com.xpg.bookstore.bookstoremain.entity.Comment;
import com.xpg.bookstore.bookstoremain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

public class CommentServiceImplDataFlowTest {

  @Mock private UserDao userDao;

  @Mock private CommentDao commentDao;

  @InjectMocks private CommentServiceImpl commentService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  // 测试like方法的数据流
  @Test
  void like_ShouldFail_WhenCommentNotFound() {
    // 定义变量
    long nonExistingCommentId = 999L;
    long userId = 1L;

    // 模拟数据流路径
    when(commentDao.findById(nonExistingCommentId)).thenReturn(null);
    when(userDao.findById(userId)).thenReturn(new User());

    // 验证数据流动
    JSONObject result = commentService.like(nonExistingCommentId, userId);

    assertEquals("评论不存在", result.getString("message"));
    verify(commentDao).findById(nonExistingCommentId);
  }

  @Test
  void like_ShouldFail_WhenUserNotFound() {
    // 定义变量
    long commentId = 1L;
    long nonExistingUserId = 999L;
    Comment comment = new Comment();

    // 模拟数据流路径
    when(commentDao.findById(commentId)).thenReturn(comment);
    when(userDao.findById(nonExistingUserId)).thenReturn(null);

    // 验证数据流动
    JSONObject result = commentService.like(commentId, nonExistingUserId);

    assertEquals("用户不存在", result.getString("message"));
    verify(userDao).findById(nonExistingUserId);
  }

  @Test
  void like_ShouldFail_WhenAlreadyLiked() {
    // 定义变量和数据流路径
    long commentId = 1L;
    long userId = 1L;

    Comment comment = new Comment();
    comment.setCommentId(commentId);

    User user = new User();
    user.setUserId(userId);

    List<Comment> likedComments = new ArrayList<>();
    likedComments.add(comment);
    user.setLikeComments(likedComments);

    List<User> likeUsers = new ArrayList<>();
    likeUsers.add(user);
    comment.setLikeUsers(likeUsers);

    // 模拟数据流
    when(commentDao.findById(commentId)).thenReturn(comment);
    when(userDao.findById(userId)).thenReturn(user);

    // 验证数据流动
    JSONObject result = commentService.like(commentId, userId);

    assertEquals("评论已点赞", result.getString("message"));
    verify(commentDao).findById(commentId);
  }

  @Test
  void like_ShouldSuccess_WhenValidData() {
    // 定义变量和数据流路径
    long commentId = 1L;
    long userId = 1L;
    int initialLikes = 5;

    Comment comment = new Comment();
    comment.setCommentId(commentId);
    comment.setLike(initialLikes);
    comment.setLikeUsers(new ArrayList<>());

    User user = new User();
    user.setUserId(userId);
    user.setLikeComments(new ArrayList<>());

    // 模拟数据流
    when(commentDao.findById(commentId)).thenReturn(comment);
    when(userDao.findById(userId)).thenReturn(user);
    when(commentDao.save(comment)).thenReturn(comment);

    // 验证数据流动
    JSONObject result = commentService.like(commentId, userId);

    assertEquals("点赞成功", result.getString("message"));
    assertEquals(initialLikes + 1, comment.getLike());
    assertTrue(comment.getLikeUsers().contains(user));
    verify(commentDao).save(comment);
  }

  // 测试unlike方法的数据流
  @Test
  void unlike_ShouldFail_WhenCommentNotFound() {
    // 定义变量
    long nonExistingCommentId = 999L;
    long userId = 1L;

    // 模拟数据流路径
    when(commentDao.findById(nonExistingCommentId)).thenReturn(null);
    when(userDao.findById(userId)).thenReturn(new User());

    // 验证数据流动
    JSONObject result = commentService.unlike(nonExistingCommentId, userId);

    assertEquals("评论不存在", result.getString("message"));
    verify(commentDao).findById(nonExistingCommentId);
  }

  @Test
  void unlike_ShouldFail_WhenUserNotFound() {
    // 定义变量
    long commentId = 1L;
    long nonExistingUserId = 999L;
    Comment comment = new Comment();

    // 模拟数据流路径
    when(commentDao.findById(commentId)).thenReturn(comment);
    when(userDao.findById(nonExistingUserId)).thenReturn(null);

    // 验证数据流动
    JSONObject result = commentService.unlike(commentId, nonExistingUserId);

    assertEquals("用户不存在", result.getString("message"));
    verify(userDao).findById(nonExistingUserId);
  }

  @Test
  void unlike_ShouldFail_WhenNotLiked() {
    // 定义变量和数据流路径
    long commentId = 1L;
    long userId = 1L;

    Comment comment = new Comment();
    comment.setCommentId(commentId);
    comment.setLikeUsers(new ArrayList<>());

    User user = new User();
    user.setUserId(userId);
    user.setLikeComments(new ArrayList<>());

    // 模拟数据流
    when(commentDao.findById(commentId)).thenReturn(comment);
    when(userDao.findById(userId)).thenReturn(user);

    // 验证数据流动
    JSONObject result = commentService.unlike(commentId, userId);

    assertEquals("评论未点赞", result.getString("message"));
    verify(commentDao).findById(commentId);
  }

  @Test
  void unlike_ShouldSuccess_WhenValidData() {
    // 定义变量和数据流路径
    long commentId = 1L;
    long userId = 1L;
    int initialLikes = 5;

    Comment comment = new Comment();
    comment.setCommentId(commentId);
    comment.setLike(initialLikes);

    User user = new User();
    user.setUserId(userId);

    List<User> likeUsers = new ArrayList<>();
    likeUsers.add(user);
    comment.setLikeUsers(likeUsers);

    List<Comment> likedComments = new ArrayList<>();
    likedComments.add(comment);
    user.setLikeComments(likedComments);

    // 模拟数据流
    when(commentDao.findById(commentId)).thenReturn(comment);
    when(userDao.findById(userId)).thenReturn(user);
    when(commentDao.save(comment)).thenReturn(comment);

    // 验证数据流动
    JSONObject result = commentService.unlike(commentId, userId);

    assertEquals("取消点赞成功", result.getString("message"));
    assertEquals(initialLikes - 1, comment.getLike());
    assertFalse(comment.getLikeUsers().contains(user));
    verify(commentDao).save(comment);
  }

  // 测试边界条件
  @Test
  void like_ShouldHandleZeroLikes() {
    // 定义变量和数据流路径
    long commentId = 1L;
    long userId = 1L;
    int initialLikes = 0;

    Comment comment = new Comment();
    comment.setCommentId(commentId);
    comment.setLike(initialLikes);
    comment.setLikeUsers(new ArrayList<>());

    User user = new User();
    user.setUserId(userId);
    user.setLikeComments(new ArrayList<>());

    // 模拟数据流
    when(commentDao.findById(commentId)).thenReturn(comment);
    when(userDao.findById(userId)).thenReturn(user);
    when(commentDao.save(comment)).thenReturn(comment);

    // 验证数据流动
    JSONObject result = commentService.like(commentId, userId);

    assertEquals("点赞成功", result.getString("message"));
    assertEquals(1, comment.getLike());
  }

  @Test
  void unlike_ShouldHandleOneLike() {
    // 定义变量和数据流路径
    long commentId = 1L;
    long userId = 1L;
    int initialLikes = 1;

    Comment comment = new Comment();
    comment.setCommentId(commentId);
    comment.setLike(initialLikes);

    User user = new User();
    user.setUserId(userId);

    List<User> likeUsers = new ArrayList<>();
    likeUsers.add(user);
    comment.setLikeUsers(likeUsers);

    List<Comment> likedComments = new ArrayList<>();
    likedComments.add(comment);
    user.setLikeComments(likedComments);

    // 模拟数据流
    when(commentDao.findById(commentId)).thenReturn(comment);
    when(userDao.findById(userId)).thenReturn(user);
    when(commentDao.save(comment)).thenReturn(comment);

    // 验证数据流动
    JSONObject result = commentService.unlike(commentId, userId);

    assertEquals("取消点赞成功", result.getString("message"));
    assertEquals(0, comment.getLike());
  }
}
