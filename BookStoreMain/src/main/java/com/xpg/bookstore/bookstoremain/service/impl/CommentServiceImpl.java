package com.xpg.bookstore.bookstoremain.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.dao.CommentDao;
import com.xpg.bookstore.bookstoremain.dao.UserDao;
import com.xpg.bookstore.bookstoremain.entity.Comment;
import com.xpg.bookstore.bookstoremain.entity.User;
import com.xpg.bookstore.bookstoremain.service.CommentService;
import com.xpg.bookstore.bookstoremain.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {
  @Autowired private UserDao userDao;
  @Autowired private CommentDao commentDao;

  @Override
  public JSONObject like(long commentId, long userId) {
    Comment comment = commentDao.findById(commentId);
    User user = userDao.findById(userId);
    if (comment == null) return Util.errorResponseJson("评论不存在");
    if (user == null) return Util.errorResponseJson("用户不存在");
    if (user.getLikeComments().contains(comment)) return Util.errorResponseJson("评论已点赞");
    comment.getLikeUsers().add(user);
    comment.setLike(comment.getLike() + 1);
    commentDao.save(comment);
    return Util.successResponseJson("点赞成功");
  }

  @Override
  public JSONObject unlike(long commentId, long userId) {
    Comment comment = commentDao.findById(commentId);
    User user = userDao.findById(userId);
    if (comment == null) return Util.errorResponseJson("评论不存在");
    if (user == null) return Util.errorResponseJson("用户不存在");
    if (!user.getLikeComments().contains(comment)) return Util.errorResponseJson("评论未点赞");
    comment.getLikeUsers().remove(user);
    comment.setLike(comment.getLike() - 1);
    commentDao.save(comment);
    return Util.successResponseJson("取消点赞成功");
  }
}
