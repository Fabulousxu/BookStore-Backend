package com.xpg.bookstore.bookstoremain.repository;

import com.xpg.bookstore.bookstoremain.entity.Category;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface CategoryRepository extends Neo4jRepository<Category, Long> {
  Category findByCode(String code);
}
