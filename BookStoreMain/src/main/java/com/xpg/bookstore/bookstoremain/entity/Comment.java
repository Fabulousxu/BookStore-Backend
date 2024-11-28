package com.xpg.bookstore.bookstoremain.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Data
@NoArgsConstructor
public class Comment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonProperty("id")
  @JSONField(name = "id")
  private long commentId;

  @ManyToOne
  @JoinColumn(name = "user_id")
  @JsonIgnore
  @JSONField(serialize = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "book_id")
  @JsonIgnore
  @JSONField(serialize = false)
  private Book book;

  private String content;

  @Column(name = "`like`")
  private int like = 0;

  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createdAt;

  @ManyToMany
  @JoinTable(
      name = "comment_like",
      joinColumns = @JoinColumn(name = "comment_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id"))
  @JsonIgnore
  @JSONField(serialize = false)
  private List<User> likeUsers;
}
