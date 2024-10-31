package com.xpg.bookstore.bookapiextension.service;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Service;

@Service
public interface BookService {
  JSONObject getAuthor(String title);
}
