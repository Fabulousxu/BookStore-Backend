package com.xpg.bookstore.bookstoremain.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "book")
@Data
@NoArgsConstructor
public class Book {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonProperty("id")
  @JSONField(name = "id")
  private long bookId;

  private String title;
  private String author;
  private String isbn;
  private String description;
  private int price;
  private int sales = 0;
  private int repertory = 0;
  @Transient private String cover = "";

  @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
  @OrderBy("createdAt DESC")
  @JsonIgnore
  @JSONField(serialize = false)
  private List<Comment> comments;
}
