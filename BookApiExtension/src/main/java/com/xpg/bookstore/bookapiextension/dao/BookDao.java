package com.xpg.bookstore.bookapiextension.dao;

import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface BookDao {
  Optional<String> getAuthor(String title);
}
