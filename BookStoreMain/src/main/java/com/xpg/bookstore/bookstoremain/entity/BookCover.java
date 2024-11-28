package com.xpg.bookstore.bookstoremain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document("book_cover")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCover {
  @MongoId private long bookId;
  private String cover;
}
