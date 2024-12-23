package com.xpg.bookstore.bookstoremain.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("/images/**")
        .addResourceLocations(
            "file:" + System.getProperty("user.dir") + "\\BookStoreMain\\src\\main\\resources\\static\\images\\");
  }
}
