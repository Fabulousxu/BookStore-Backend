package com.xpg.bookstore.bookstoremain.dao.impl;

import com.xpg.bookstore.bookstoremain.entity.User;
import com.xpg.bookstore.bookstoremain.repository.UserAuthRepository;
import com.xpg.bookstore.bookstoremain.dao.UserDao;
import com.xpg.bookstore.bookstoremain.entity.UserAuth;
import com.xpg.bookstore.bookstoremain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl implements UserDao {
  @Autowired private UserRepository userRepository;
  @Autowired private UserAuthRepository userAuthRepository;

  @Override
  public User save(User user) {
    return userRepository.save(user);
  }

  @Override
  public UserAuth save(UserAuth userAuth) {
    return userAuthRepository.save(userAuth);
  }

  @Override
  public User findById(long id) {
    return userRepository.findById(id).orElse(null);
  }

  @Override
  public User findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  @Override
  public boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  @Override
  public boolean existsByUsernameAndPassword(String username, String password) {
    return userAuthRepository.existsByUser_UsernameAndPassword(username, password);
  }

  @Override
  public Page<User> findByKeyword(String keyword, Pageable pageable) {
    return userRepository.findByUsernameContainsOrNicknameContainsOrEmailContains(
        keyword, keyword, keyword, pageable);
  }
}
