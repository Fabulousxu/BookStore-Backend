package com.xpg.bookstore.bookstoremain.entity;

import com.alibaba.fastjson2.JSONObject;
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
  private long userId;

  private String username;
  private String nickname = "";
  private String email;
  private long balance = 0;
  private Boolean admin = false;
  private Boolean silence = false;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @OrderBy("createdAt DESC")
  private List<Order> orders;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @OrderBy("cartItemId DESC")
  private List<CartItem> cart;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private List<Comment> comments;

  @ManyToMany(mappedBy = "likeUsers", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<Comment> likeComments;

  public User(String username, String email) {
    this.username = username;
    this.email = email;
  }

  public JSONObject toJson() {
    JSONObject json = new JSONObject();
    json.put("id", userId);
    json.put("username", username);
    json.put("nickname", nickname);
    json.put("email", email);
    json.put("balance", balance);
    json.put("silence", silence);
    return json;
  }
}
