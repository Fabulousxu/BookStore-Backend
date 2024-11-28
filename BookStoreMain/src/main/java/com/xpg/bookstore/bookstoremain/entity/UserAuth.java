package com.xpg.bookstore.bookstoremain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_auth")
@Data
@NoArgsConstructor
public class UserAuth {
  @Id private long userId;
  private String password;

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;
}
