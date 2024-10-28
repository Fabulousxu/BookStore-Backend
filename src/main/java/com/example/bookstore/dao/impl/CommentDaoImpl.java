package com.example.bookstore.dao.impl;

import com.example.bookstore.dao.CommentDao;
import com.example.bookstore.entity.Comment;
import com.example.bookstore.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class CommentDaoImpl implements CommentDao {
  @Autowired private CommentRepository commentRepository;

  @Override
  public Comment save(Comment comment) {
    return commentRepository.save(comment);
  }

  @Override
  public Comment findById(long id) {
    return commentRepository.findById(id).orElse(null);
  }

  @Override
  public Page<Comment> findByBookId(long bookId, Pageable pageable) {
    return commentRepository.findByBook_BookIdOrderByCreatedAtDesc(bookId, pageable);
  }
}
