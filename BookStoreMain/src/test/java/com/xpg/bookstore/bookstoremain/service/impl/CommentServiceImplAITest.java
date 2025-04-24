package com.xpg.bookstore.bookstoremain.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.dao.CommentDao;
import com.xpg.bookstore.bookstoremain.dao.UserDao;
import com.xpg.bookstore.bookstoremain.entity.Comment;
import com.xpg.bookstore.bookstoremain.entity.User;
import com.xpg.bookstore.bookstoremain.util.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplAITest {

  @Mock private CommentDao commentDao;

  @Mock private UserDao userDao;

  @InjectMocks private CommentServiceImpl commentService;

  private Comment comment;
  private User user;

  @BeforeEach
  void setUp() {
    comment = new Comment();
    comment.setCommentId(1L);
    comment.setLike(0);
    comment.setLikeUsers(new ArrayList<>());

    user = new User();
    user.setUserId(1L);
    user.setLikeComments(new ArrayList<>());
  }

  // 参数化测试：点赞成功
  @ParameterizedTest
  @MethodSource("provideValidCommentAndUserIds")
  void testLikeSuccess(long commentId, long userId) {
    when(commentDao.findById(commentId)).thenReturn(comment);
    when(userDao.findById(userId)).thenReturn(user);

    JSONObject response = commentService.like(commentId, userId);

    assertEquals(Util.successResponseJson("点赞成功"), response);
    assertEquals(1, comment.getLike());
    verify(commentDao, times(1)).save(comment);
  }

  // 参数化测试：点赞失败（评论不存在）
  @ParameterizedTest
  @MethodSource("provideInvalidCommentIds")
  void testLikeCommentNotFound(long commentId) {
    when(commentDao.findById(commentId)).thenReturn(null);

    JSONObject response = commentService.like(commentId, 1L);

    assertEquals(Util.errorResponseJson("评论不存在"), response);
  }

  // 参数化测试：点赞失败（用户不存在）
  @ParameterizedTest
  @MethodSource("provideInvalidUserIds")
  void testLikeUserNotFound(long userId) {
    when(commentDao.findById(1L)).thenReturn(comment);
    when(userDao.findById(userId)).thenReturn(null);

    JSONObject response = commentService.like(1L, userId);

    assertEquals(Util.errorResponseJson("用户不存在"), response);
  }

  // 参数化测试：取消点赞成功
  @ParameterizedTest
  @MethodSource("provideValidCommentAndUserIds")
  void testUnlikeSuccess(long commentId, long userId) {
    when(commentDao.findById(commentId)).thenReturn(comment);
    when(userDao.findById(userId)).thenReturn(user);
    user.getLikeComments().add(comment);
    comment.getLikeUsers().add(user);
    comment.setLike(1);

    JSONObject response = commentService.unlike(commentId, userId);

    assertEquals(Util.successResponseJson("取消点赞成功"), response);
    assertEquals(0, comment.getLike());
    verify(commentDao, times(1)).save(comment);
  }

  // 参数化测试：取消点赞失败（评论不存在）
  @ParameterizedTest
  @MethodSource("provideInvalidCommentIds")
  void testUnlikeCommentNotFound(long commentId) {
    when(commentDao.findById(commentId)).thenReturn(null);

    JSONObject response = commentService.unlike(commentId, 1L);

    assertEquals(Util.errorResponseJson("评论不存在"), response);
  }

  // 参数化测试：取消点赞失败（用户不存在）
  @ParameterizedTest
  @MethodSource("provideInvalidUserIds")
  void testUnlikeUserNotFound(long userId) {
    when(commentDao.findById(1L)).thenReturn(comment);
    when(userDao.findById(userId)).thenReturn(null);

    JSONObject response = commentService.unlike(1L, userId);

    assertEquals(Util.errorResponseJson("用户不存在"), response);
  }

  // 提供有效的评论ID和用户ID组合
  static Stream<Arguments> provideValidCommentAndUserIds() {
    return Stream.of(Arguments.of(1L, 1L), Arguments.of(2L, 2L), Arguments.of(3L, 3L));
  }

  // 提供无效的评论ID
  static Stream<Arguments> provideInvalidCommentIds() {
    return Stream.of(
        Arguments.of(999L), // 不存在的评论ID
        Arguments.of(0L) // 无效的评论ID
        );
  }

  // 提供无效的用户ID
  static Stream<Arguments> provideInvalidUserIds() {
    return Stream.of(
        Arguments.of(999L), // 不存在的用户ID
        Arguments.of(0L) // 无效的用户ID
        );
  }
}
