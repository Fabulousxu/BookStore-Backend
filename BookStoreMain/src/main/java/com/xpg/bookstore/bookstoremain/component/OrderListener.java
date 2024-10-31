package com.xpg.bookstore.bookstoremain.component;

import com.alibaba.fastjson2.JSONObject;
import com.xpg.bookstore.bookstoremain.service.OrderService;
import java.util.ArrayList;
import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderListener {
  @Autowired private OrderService orderService;
  @Autowired private KafkaTemplate<String, String> kafkaTemplate;
  @Autowired private OrderWebSocket ws;

  @KafkaListener(topics = "placeOrder", groupId = "bookstore")
  public void placeOrderListener(ConsumerRecord<String, String> record)
      throws InterruptedException {
    System.out.println("Place order: " + record.value());
    JSONObject body = JSONObject.parseObject(record.value());
    List<Long> items = new ArrayList<>();
    for (int i = 0; i < body.getJSONArray("itemIds").size(); i++)
      items.add(body.getJSONArray("itemIds").getLong(i));
    Thread.sleep(5000); // Simulate decay
    JSONObject res =
        orderService.placeOrder(
            items,
            body.getLongValue("userId"),
            body.getString("receiver"),
            body.getString("address"),
            body.getString("tel"));
    res.put("id", body.getLongValue("userId"));
    kafkaTemplate.send("placeOrderResult", res.toJSONString());
  }

  @KafkaListener(topics = "placeOrderResult", groupId = "bookstore")
  public void placeOrderResultListener(ConsumerRecord<String, String> record) {
    System.out.println("Place order result: " + record.value());
    JSONObject res = JSONObject.parseObject(record.value());
    ws.sendMessage(res.getLongValue("id"), res.toJSONString());
  }
}
