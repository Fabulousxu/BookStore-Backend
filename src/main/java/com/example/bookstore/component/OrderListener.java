package com.example.bookstore.component;

import com.alibaba.fastjson2.JSONObject;
import com.example.bookstore.service.OrderService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderListener {
  @Autowired private OrderService orderService;
  @Autowired private KafkaTemplate<String, String> kafkaTemplate;

  @KafkaListener(topics = "placeOrder", groupId = "bookstore")
  public void placeOrderListener(ConsumerRecord<String, String> record) {
    System.out.println("Place order: " + record.value());
    JSONObject body = JSONObject.parseObject(record.value());
    List<Long> items = new ArrayList<>();
    for (int i = 0; i < body.getJSONArray("itemIds").size(); i++)
      items.add(body.getJSONArray("itemIds").getLong(i));
    JSONObject res =
        orderService.placeOrder(
            items,
            body.getLongValue("userId"),
            body.getString("receiver"),
            body.getString("address"),
            body.getString("tel"));
    kafkaTemplate.send("placeOrderResult", res.toJSONString());
  }

  @KafkaListener(topics = "placeOrderResult", groupId = "bookstore")
  public void placeOrderResultListener(ConsumerRecord<String, String> record) {
    System.out.println("Place order result: " + record.value());
  }
}
