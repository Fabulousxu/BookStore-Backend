package com.xpg.bookstore.bookstoremain.entity;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node
@Data
@NoArgsConstructor
public class Category {
  @Id @GeneratedValue private long id;

  @Indexed(unique = true)
  private String code;

  private String name;

  @Relationship(type = "SUBCATEGORY")
  private List<Category> subCategories;

  public String getParentCode() {
    if (code.length() == 1) return "root";
    String parentCode = code.substring(0, code.length() - 1);
    if (parentCode.charAt(parentCode.length() - 1) == '.'
        || parentCode.charAt(parentCode.length() - 1) == '-')
      parentCode = parentCode.substring(0, parentCode.length() - 1);
    return parentCode;
  }
}
