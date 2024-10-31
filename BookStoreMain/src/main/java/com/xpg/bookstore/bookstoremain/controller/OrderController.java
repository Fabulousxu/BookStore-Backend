package com.xpg.bookstore.bookstoremain.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.service.OrderService;
import com.xpg.bookstore.bookstoremain.util.Util;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@CrossOrigin
public class OrderController {
  private static final boolean PLACE_ORDER_USE_KAFKA = true;
  @Autowired private OrderService orderService;
  @Autowired private KafkaTemplate<String, String> kafkaTemplate;

  @GetMapping
  public JSONArray getOrderItems(@SessionAttribute("id") long id, String keyword) {
    return orderService.getOrderItems(id, keyword);
  }

  @PostMapping
  public JSONObject placeOrder(@RequestBody JSONObject body, @SessionAttribute("id") long id) {
    if (PLACE_ORDER_USE_KAFKA) {
      body.put("userId", id);
      var res = Util.successResponseJson("订单处理中");
      res.put("ws", true);
      res.put("id", id);
      kafkaTemplate.send("placeOrder", body.toJSONString());
      return res;
    } else {
      List<Long> items = new ArrayList<>();
      for (int i = 0; i < body.getJSONArray("itemIds").size(); i++)
        items.add(body.getJSONArray("itemIds").getLong(i));
      var res =
          orderService.placeOrder(
              items,
              id,
              body.getString("receiver"),
              body.getString("address"),
              body.getString("tel"));
      res.put("ws", false);
      return res;
    }
  }
}
