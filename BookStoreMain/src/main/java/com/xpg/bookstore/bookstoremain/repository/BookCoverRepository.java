package com.xpg.bookstore.bookstoremain.repository;

import com.xpg.bookstore.bookstoremain.entity.BookCover;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookCoverRepository extends MongoRepository<BookCover, String> {}
