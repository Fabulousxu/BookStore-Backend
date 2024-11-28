package com.xpg.bookstore.bookstoremain.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonProperty("id")
  @JSONField(name = "id")
  private long userId;

  private String username;
  private String nickname = "";
  private String email;
  private long balance = 0;
  private Boolean silence = false;

  @JsonIgnore
  @JSONField(serialize = false)
  private Boolean admin = false;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("createdAt DESC")
  @JsonIgnore
  @JSONField(serialize = false)
  private List<Order> orders;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("cartItemId DESC")
  @JsonIgnore
  @JSONField(serialize = false)
  private List<CartItem> cart;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  @JSONField(serialize = false)
  private List<Comment> comments;

  @ManyToMany(mappedBy = "likeUsers", cascade = CascadeType.ALL)
  @JsonIgnore
  @JSONField(serialize = false)
  private List<Comment> likeComments;
}
