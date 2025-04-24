package com.xpg.bookstore.bookstoremain.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.dao.CommentDao;
import com.xpg.bookstore.bookstoremain.dao.UserDao;
import com.xpg.bookstore.bookstoremain.entity.Comment;
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
class CommentServiceImplDUPathTest {
  @InjectMocks private CommentServiceImpl commentService;
  @Mock private UserDao userDao;
  @Mock private CommentDao commentDao;

  @Test
  void like() {
    User user = new User();
    Comment comment1 = new Comment();
    comment1.setCommentId(1);
    comment1.setLikeUsers(new ArrayList<>());
    Comment comment2 = new Comment();
    comment2.setCommentId(2);
    comment2.setLikeUsers(new ArrayList<>());
    List<Comment> comments = new ArrayList<>();
    comments.add(comment1);
    user.setLikeComments(comments);
    when(userDao.findById(eq(1L))).thenReturn(user);
    when(userDao.findById(eq(2L))).thenReturn(null);
    when(commentDao.findById(1L)).thenReturn(comment1);
    when(commentDao.findById(2L)).thenReturn(comment2);
    when(commentDao.findById(3L)).thenReturn(null);
    when(commentDao.save(any(Comment.class))).thenReturn(comment1);

    JSONObject noneUserResult = commentService.like(1L, 2L);
    assertFalse(noneUserResult.getBoolean("ok"));
    JSONObject noneCommentResult = commentService.like(3L, 1L);
    assertFalse(noneCommentResult.getBoolean("ok"));
    JSONObject alreadyLikedResult = commentService.like(1L, 1L);
    assertFalse(alreadyLikedResult.getBoolean("ok"));
    JSONObject successResult = commentService.like(2L, 1L);
    assertTrue(successResult.getBoolean("ok"));
  }

  @Test
  void unlike() {
    User user = new User();
    Comment comment1 = new Comment();
    comment1.setCommentId(1);
    comment1.setLikeUsers(new ArrayList<>());
    Comment comment2 = new Comment();
    comment2.setCommentId(2);
    comment2.setLikeUsers(new ArrayList<>());
    List<Comment> comments = new ArrayList<>();
    comments.add(comment1);
    user.setLikeComments(comments);
    when(userDao.findById(eq(1L))).thenReturn(user);
    when(userDao.findById(eq(2L))).thenReturn(null);
    when(commentDao.findById(1L)).thenReturn(comment1);
    when(commentDao.findById(2L)).thenReturn(comment2);
    when(commentDao.findById(3L)).thenReturn(null);
    when(commentDao.save(any(Comment.class))).thenReturn(comment1);

    JSONObject noneUserResult = commentService.unlike(1L, 2L);
    assertFalse(noneUserResult.getBoolean("ok"));
    JSONObject noneCommentResult = commentService.unlike(3L, 1L);
    assertFalse(noneCommentResult.getBoolean("ok"));
    JSONObject notLikedResult = commentService.unlike(2L, 1L);
    assertFalse(notLikedResult.getBoolean("ok"));
    JSONObject successResult = commentService.unlike(1L, 1L);
    assertTrue(successResult.getBoolean("ok"));
  }
}
