package com.xpg.bookstore.bookstoremain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
    excludeFilters = {
      @ComponentScan.Filter(
          type = FilterType.REGEX,
          pattern = {"com.xpg.bookstore.bookstoremain.component.OrderListener"})
    })
public class BookStoreMainApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookStoreMainApplication.class, args);
	}

}
