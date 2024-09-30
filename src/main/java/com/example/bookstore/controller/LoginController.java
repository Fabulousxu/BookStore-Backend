package com.example.bookstore.controller;

import com.alibaba.fastjson2.JSONObject;
import com.example.bookstore.service.LoginTimerService;
import com.example.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
@Scope("session")
public class LoginController {
  @Autowired private UserService userService;
  @Autowired private LoginTimerService loginTimerService;

  @PostMapping("/login")
  public JSONObject login(@RequestBody JSONObject body) {
    loginTimerService.login();
    return userService.login(body.getString("username"), body.getString("password"));
  }

  @PostMapping("/logout")
  public JSONObject logout(@SessionAttribute("id") long userId) {
    JSONObject res = userService.logout(userId);
    long loginTime = loginTimerService.logout() / 1000;
    String formatTime =
      String.format("%d:%02d:%02d", loginTime / 3600, loginTime % 3600 / 60, loginTime % 60);
    JSONObject data = new JSONObject();
    data.put("time", formatTime);
    res.put("data", data);
    return res;
  }

  @PostMapping("/register")
  public JSONObject register(@RequestBody JSONObject body) {
    return userService.register(
        body.getString("username"), body.getString("email"), body.getString("password"));
  }
}
