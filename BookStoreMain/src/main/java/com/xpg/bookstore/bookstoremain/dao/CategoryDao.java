package com.xpg.bookstore.bookstoremain.dao;

import com.xpg.bookstore.bookstoremain.entity.Category;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryDao {
  Category findByCode(String code);
}
