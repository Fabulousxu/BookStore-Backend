package com.xpg.bookstore.bookstoremain.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_item")
@Data
@NoArgsConstructor
public class OrderItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonProperty("id")
  @JSONField(name = "id")
  private long orderItemId;

  @ManyToOne
  @JoinColumn(name = "order_id")
  @JsonIgnore
  @JSONField(serialize = false)
  private Order order;

  @ManyToOne
  @JoinColumn(name = "book_id")
  private Book book;

  private int number;
}
