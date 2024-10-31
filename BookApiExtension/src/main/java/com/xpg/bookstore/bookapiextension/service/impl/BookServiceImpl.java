package com.xpg.bookstore.bookapiextension.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookapiextension.dao.BookDao;
import com.xpg.bookstore.bookapiextension.service.BookService;
import com.xpg.bookstore.bookapiextension.util.Util;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {
  @Autowired private BookDao bookDao;

  @Override
  public JSONObject getAuthor(String title) {
    Optional<String> author = bookDao.getAuthor(title);
    if (author.isPresent()) {
      var res = Util.successResponseJson("");
      res.put("author", author.get());
      return res;
    } else return Util.errorResponseJson("《" + title + "》不存在！");
  }
}
