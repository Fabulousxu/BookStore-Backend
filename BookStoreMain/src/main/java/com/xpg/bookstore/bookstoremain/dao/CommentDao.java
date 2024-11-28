package com.xpg.bookstore.bookstoremain.dao;

import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentDao {
  Comment save(Comment comment);

  Comment findById(long id);

  Page<Comment> findByBookId(long bookId, Pageable pageable);

  JSONObject addMessageToJson(Comment comment, long userId);
}
