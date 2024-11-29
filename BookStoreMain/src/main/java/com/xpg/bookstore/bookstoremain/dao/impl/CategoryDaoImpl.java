package com.xpg.bookstore.bookstoremain.dao.impl;

import com.xpg.bookstore.bookstoremain.dao.CategoryDao;
import com.xpg.bookstore.bookstoremain.entity.Category;
import com.xpg.bookstore.bookstoremain.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryDaoImpl implements CategoryDao {
  @Autowired private CategoryRepository categoryRepository;

  @Override
  public Category findByCode(String code) {
    return categoryRepository.findByCode(code);
  }
}
