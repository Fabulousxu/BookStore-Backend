package com.example.bookstore.dao;

import com.example.bookstore.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentDao {
  Comment save(Comment comment);

  Comment findById(long id);

  Page<Comment> findByBookId(long bookId, Pageable pageable);
}
