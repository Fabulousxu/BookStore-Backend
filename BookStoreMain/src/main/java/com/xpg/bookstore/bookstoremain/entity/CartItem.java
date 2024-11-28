package com.xpg.bookstore.bookstoremain.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart_item")
@Data
@NoArgsConstructor
public class CartItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonProperty("id")
  @JSONField(name = "id")
  private long cartItemId;

  @ManyToOne
  @JoinColumn(name = "user_id")
  @JsonIgnore
  @JSONField(serialize = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "book_id")
  private Book book;

  private int number = 1;
}
