package com.xpg.bookstore.bookapiextension.dao.impl;

import com.xpg.bookstore.bookapiextension.dao.BookDao;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BookDaoImpl implements BookDao {
  @Autowired private JdbcTemplate jdbcTemplate;

  @Override
  public Optional<String> getAuthor(String title) {
    return jdbcTemplate.query(
        "select author from book where title = ?",
        new Object[] {title},
        res -> {
          return res.next() ? Optional.of(res.getString("author")) : Optional.empty();
        });
  }
}
