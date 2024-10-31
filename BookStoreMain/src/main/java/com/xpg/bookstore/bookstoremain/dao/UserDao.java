package com.xpg.bookstore.bookstoremain.dao;

import com.xpg.bookstore.bookstoremain.entity.User;
import com.xpg.bookstore.bookstoremain.entity.UserAuth;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao {
  User save(User user);

  UserAuth save(UserAuth userAuth);

  User findById(long id);

  User findByUsername(String username);

  boolean existsByUsername(String username);

  boolean existsByUsernameAndPassword(String username, String password);

  Page<User> findByKeyword(String keyword, Pageable pageable);
}
