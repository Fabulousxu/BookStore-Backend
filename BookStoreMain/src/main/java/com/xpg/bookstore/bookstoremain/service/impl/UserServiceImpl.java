package com.xpg.bookstore.bookstoremain.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.dao.UserDao;
import com.xpg.bookstore.bookstoremain.entity.User;
import com.xpg.bookstore.bookstoremain.entity.UserAuth;
import com.xpg.bookstore.bookstoremain.service.UserService;
import com.xpg.bookstore.bookstoremain.util.Util;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
  @Autowired private UserDao userDao;
  @Autowired private HttpSession session;

  @Override
  public JSONObject login(String username, String password) {
    User user = userDao.findByUsername(username);
    if (user == null) return Util.errorResponseJson("用户不存在");
    if (!userDao.existsByUsernameAndPassword(username, password))
      return Util.errorResponseJson("密码错误");
    if (user.getSilence()) return Util.errorResponseJson("您的账户已被禁用");
    session.setAttribute("id", user.getUserId());
    JSONObject res = Util.successResponseJson("登录成功"), data = new JSONObject();
    data.put("admin", user.getAdmin());
    res.put("data", data);
    return res;
  }

  @Override
  public JSONObject logout(long userId) {
    session.removeAttribute("id");
    return Util.successResponseJson("登出成功");
  }

  @Override
  public JSONObject register(String username, String email, String password) {
    if (userDao.existsByUsername(username)) return Util.errorResponseJson("用户已存在");
    User user = new User();
    user.setUsername(username);
    user.setEmail(email);
    userDao.save(user);
    UserAuth userAuth = new UserAuth();
    userAuth.setUserId(user.getUserId());
    userAuth.setPassword(password);
    userDao.save(userAuth);
    return Util.successResponseJson("注册成功");
  }

  @Override
  public JSONObject searchUsers(String keyword, int pageIndex, int pageSize) {
    JSONObject res = new JSONObject();
    Page<User> userPage = userDao.findByKeyword(keyword, PageRequest.of(pageIndex, pageSize));
    res.put("totalNumber", userPage.getTotalElements());
    res.put("totalPage", userPage.getTotalPages());
    JSONArray items = new JSONArray();
    for (User user : userPage) items.add(user);
    res.put("items", items);
    return res;
  }

  @Override
  public JSONObject setUserInfo(long userId, String username, String email) {
    User user = userDao.findById(userId);
    if (user == null) return Util.errorResponseJson("用户不存在");
    user.setUsername(username);
    user.setEmail(email);
    userDao.save(user);
    return Util.successResponseJson("修改成功");
  }

  @Override
  public JSONObject silenceUser(long userId) {
    User user = userDao.findById(userId);
    if (user == null) return Util.errorResponseJson("用户不存在");
    if (user.getSilence()) return Util.errorResponseJson("用户已被禁用");
    user.setSilence(true);
    userDao.save(user);
    return Util.successResponseJson("禁用成功");
  }

  @Override
  public JSONObject unsilenceUser(long userId) {
    User user = userDao.findById(userId);
    if (user == null) return Util.errorResponseJson("用户不存在");
    if (!user.getSilence()) return Util.errorResponseJson("用户未被禁用");
    user.setSilence(false);
    userDao.save(user);
    return Util.successResponseJson("解禁成功");
  }
}
